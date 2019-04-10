package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.common.model._
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.{LogAndContinueExceptionHandler, LogAndFailExceptionHandler}
import org.apache.kafka.streams.kstream.{Printed, Produced}
import org.apache.kafka.streams.scala.kstream.Consumed
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import FoodOrder.{BreakfastFormat, DinnerFormat, DrinkFormat, LunchFormat}

import scala.collection.JavaConverters._


/**
  * Created by loicmdivad.
  */
object Demo extends App with DemoImplicits {

  val config = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "answer-one-breakfast",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG -> classOf[LogAndFailExceptionHandler]//default
  )

  val avroSede = new GenericAvroSerde()
  avroSede.configure(Map("schema.registry.url" -> "http://localhost:8081").asJava, false)

  implicit val consumed: Consumed[Bytes, FoodOrder] = Consumed.`with`(Serdes.Bytes, FoodOrderSerde.foodSerde)
  implicit val produced: Produced[Bytes, GenericRecord] = Produced.`with`(Serdes.Bytes, avroSede)

  val builder: StreamsBuilder = new StreamsBuilder()

  val Array(breakfasts, lunches, drinks, dinners, others) = builder

    .stream[Bytes, FoodOrder]("input-food-order")(consumed)

    .branch(
      (_, value) => value.isInstanceOf[Breakfast],
      (_, value) => value.isInstanceOf[Lunch],
      (_, value) => value.isInstanceOf[Drink],
      (_, value) => value.isInstanceOf[Dinner],
      (_, _) => true
    )

  breakfasts.print(Printed.toSysOut[Bytes, FoodOrder].withLabel("breakfast"))
  lunches.print(Printed.toSysOut[Bytes, FoodOrder].withLabel("lunch"))
  drinks.print(Printed.toSysOut[Bytes, FoodOrder].withLabel("drink"))
  dinners.print(Printed.toSysOut[Bytes, FoodOrder].withLabel("drink"))

  breakfasts.mapValues(food => food.toAvro[Breakfast]).to("decoded-breakfast")

  lunches.mapValues(food => food.toAvro[Lunch]).to("decoded-lunch")

  drinks.mapValues(food => food.toAvro[Drink]).to("decoded-drink")

  dinners.mapValues(food => food.toAvro[Dinner]).to("decoded-dinner")

  others.to("decoded-other")(Produced.`with`(Serdes.Bytes, FoodOrderSerde.foodSerde))


  val streams: KafkaStreams = new KafkaStreams(builder.build(), config.toProperties)

  streams.cleanUp()

  streams.start()

  sys.ShutdownHookThread {
    streams.close()
    streams.cleanUp()
  }
}
