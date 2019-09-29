package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.common.model._
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.{LogAndContinueExceptionHandler, LogAndFailExceptionHandler}
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import fr.xebia.ldi.ratatouille.handler.DeadLetterQueueFoodExceptionHandler
import fr.xebia.ldi.ratatouille.processor.FoodOrderSentinelValueProcessor
import fr.xebia.ldi.ratatouille.serde.SentinelValueSerde
import fr.xebia.ldi.ratatouille.serde.SentinelValueSerde.FoodOrderError
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
  * Created by loicmdivad.
  */
object Demo extends App with DemoImplicits {

  val logger = LoggerFactory.getLogger(getClass)

  val config = Map(
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.APPLICATION_ID_CONFIG -> "kafka-summit-2019"
  ) ++ monitoringConfigs

  val avroSerde: GenericAvroSerde = new GenericAvroSerde()
  avroSerde.configure(Map(SCHEMA_REGISTRY_URL_CONFIG -> "http://localhost:8081").asJava, false)
  val consumed: Consumed[Bytes, FoodOrder] = Consumed.`with`(Serdes.Bytes, FoodOrderSerde.foodSerde)
  val produced: Produced[Bytes, GenericRecord] = Produced.`with`(Serdes.Bytes, avroSerde)

  val builder = new StreamsBuilder()

  val Array(breakfasts, lunches, drinks, errors, other) = builder

    .stream[Bytes, FoodOrder]("input-food-order")(consumed)

    .branch(
      (_, value) => value.isInstanceOf[Breakfast],
      (_, value) => value.isInstanceOf[Lunch],
      (_, value) => value.isInstanceOf[Drink],
      (_, value) => value equals FoodOrderError,
      (_, _) => false
    )

  val _ = {
    breakfasts  print   Printed.toSysOut[Bytes, FoodOrder]    .withLabel(`🥐Label`)
    lunches     print   Printed.toSysOut[Bytes, FoodOrder]    .withLabel(`🍕Label`)
    drinks      print   Printed.toSysOut[Bytes, FoodOrder]    .withLabel(`🍺Label`)
    //dinners     //print   Printed.toSysOut[Bytes, FoodOrder]    //.withLabel(`🍝Label`)
  }

  breakfasts /* processing */ .mapValues(_.toAvro).to("decoded-breakfast")(produced)

  lunches /* processing */ .mapValues(_.toAvro).to("decoded-lunch")(produced)

  drinks /* processing */ .mapValues(_.toAvro).to("decoded-drink")(produced)

  errors.transformValues(() => new FoodOrderSentinelValueProcessor())

  val topology: Topology = builder.build()
  val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
  logger debug topology.describe().toString

  sys.ShutdownHookThread {
      logger error "☠️☠️ closing the streaming app ☠️☠️"
      streams.close()
  }

  streams.cleanUp()
  streams.start()

}
