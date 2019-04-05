package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.model.Lunch
import fr.xebia.ldi.ratatouille.model.Lunch.{LunchError, dishToString}
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import purecsv.safe._

/**
  * Created by loicmdivad.
  */
object ExampleTwo extends App with Example {

  val config = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "answer-two-lunch",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG -> classOf[LogAndContinueExceptionHandler]
  )

  val builder: StreamsBuilder = new StreamsBuilder()

  val Array(error, meals) = builder

    .stream[String, String]("exercise-lunch")(Consumed.`with`(Serdes.String, Serdes.String))

    .flatMapValues { line =>

      CSVReader[Lunch]

        .readCSVFromString(line, ',')

        .map(_.getOrElse(new LunchError(line)))
    }

    .branch(
      (_, lunch) => lunch.isInstanceOf[LunchError],
      (_, _) => true,
    )

  error.foreach((_, err) => logger.warn(err.toString))

  meals
    .map((_, meal) => (dishToString.to(meal.`type`), meal.price))

    .print(Printed.toSysOut[String, Double])

  val streams: KafkaStreams = new KafkaStreams(builder.build(), config.toProperties)

  streams.cleanUp()

  streams.start()

  sys.ShutdownHookThread {
    streams.close()
    streams.cleanUp()
  }
}
