package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.common.model.{Breakfast, FoodOrder}
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.Consumed
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}


/**
  * Created by loicmdivad.
  */
object Demo extends App with DemoImplicits {

  val config = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "answer-one-breakfast",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG -> classOf[LogAndContinueExceptionHandler]
  )

  val consumed: Consumed[Bytes, FoodOrder] = Consumed.`with`(Serdes.Bytes, FoodOrderSerde.foodSerde)

  val builder: StreamsBuilder = new StreamsBuilder()

  val Array(breakfasts, food) = builder

    .stream[Bytes, FoodOrder]("exercise-breakfast")(consumed)

    .branch(
      (_, value) => value.isInstanceOf[Breakfast],
      (_, _) => true
    )

  breakfasts

    .mapValues { meal =>
      meal match {
        case command: Breakfast => command.toString
      }
    }

    .print(Printed.toSysOut[Bytes, String])


  val streams: KafkaStreams = new KafkaStreams(builder.build(), config.toProperties)

  streams.cleanUp()

  streams.start()

  sys.ShutdownHookThread {
    streams.close()
    streams.cleanUp()
  }
}
