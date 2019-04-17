package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.common.model.FoodOrder.{BreakfastFormat, DinnerFormat, DrinkFormat, LunchFormat}
import fr.xebia.ldi.ratatouille.common.model._
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import fr.xebia.ldi.ratatouille.processor.FoodOrderErrorSink
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}
import org.apache.kafka.streams.errors.{LogAndContinueExceptionHandler, LogAndFailExceptionHandler}
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.{Consumed, KStream, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import FoodOrder.{BreakfastFormat, DinnerFormat, DrinkFormat, LunchFormat}
import fr.xebia.ldi.ratatouille.handler.DeadLetterQueueFoodExceptionHandler
import fr.xebia.ldi.ratatouille.processor.FoodOrderErrorSink
import fr.xebia.ldi.ratatouille.serde.SentinelValueSerde
import fr.xebia.ldi.ratatouille.serde.SentinelValueSerde.FoodOrderErr
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.BytesSerializer

import scala.collection.JavaConverters._


/**
  * Created by loicmdivad.
  */
object Demo extends App with DemoImplicits {

  val config = Map(
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.APPLICATION_ID_CONFIG -> "devoxx-2019-appid",
    // s"dlq.topic.name" -> "dlq-food-order",
    // s"dlq.schema.registry.url" -> "http://localhost:8081",
    // s"dlq.${StreamsConfig.BOOTSTRAP_SERVERS_CONFIG}" -> "localhost:9092",
    // s"dlq.${ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG}" -> classOf[BytesSerializer],
    // s"dlq.${ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG}" -> classOf[GenericAvroSerializer],
    StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG -> classOf[LogAndFailExceptionHandler]
  )

  val avroSede = new GenericAvroSerde()
  avroSede.configure(Map("schema.registry.url" -> "http://localhost:8081").asJava, false)

  implicit val consumed: Consumed[Bytes, FoodOrder] = Consumed.`with`(Serdes.Bytes, FoodOrderSerde.foodSerde)
  implicit val produced: Produced[Bytes, GenericRecord] = Produced.`with`(Serdes.Bytes, avroSede)

  val builder: StreamsBuilder = new StreamsBuilder()

  val kstreams: KStream[Bytes, FoodOrder] = builder.stream[Bytes, FoodOrder]("input-food-order")

  val Array(breakfasts, lunches, drinks, errors, other) = kstreams.branch(
    (_, value) => value.isInstanceOf[Breakfast],
    (_, value) => value.isInstanceOf[Lunch],
    (_, value) => value.isInstanceOf[Drink],
    (_, value) => value equals FoodOrderErr,
    (_, _) => true
  )

  breakfasts.print(Printed.toSysOut[Bytes, FoodOrder].withLabel(BreakfastLabel))
  lunches.print(Printed.toSysOut[Bytes, FoodOrder].withLabel(LunchLabel))
  drinks.print(Printed.toSysOut[Bytes, FoodOrder].withLabel(DrinkLabel))
  // dinners.print(Printed.toSysOut[Bytes, FoodOrder].withLabel(DinnerLabel))

  breakfasts.mapValues(_.toAvro[Breakfast]).to("decoded-breakfast")

  lunches.mapValues(_.toAvro[Lunch]).to("decoded-lunch")

  drinks.mapValues(_.toAvro[Drink]).to("decoded-drink")

  errors.transformValues(() => new FoodOrderErrorSink)

  // produce avro .to("decoded-dinner")

  // others.to("decoded-other")(Produced.`with`(Serdes.Bytes, FoodOrderSerde.foodSerde))

  val streams: KafkaStreams = new KafkaStreams(builder.build(), config.toProperties)

  streams.cleanUp()

  streams.start()

  sys.ShutdownHookThread {
    streams.close()
    streams.cleanUp()
  }
}
