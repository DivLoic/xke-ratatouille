package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.processor.WrongJsonSink
import fr.xebia.ldi.ratatouille.serdes.JsonSerdes
import io.circe._
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.scala.kstream.{Consumed, KStream, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

/**
  * Created by loicmdivad.
  */
object ExampleThree extends App with Example {

  val config = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "answer-three-lunch",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092"
  )

  implicit val consumed: Consumed[Bytes, Json] = Consumed.`with`(Serdes.Bytes , JsonSerdes.serdes)

  implicit val produced: Produced[Bytes, Json] = Produced.`with`(Serdes.Bytes , JsonSerdes.serdes)

  val builder: StreamsBuilder = new StreamsBuilder()

  val Array(errors, input) = builder.stream[Bytes, Json]("exercise-drink").branch(
    (_, value) => value.isNull ,
    (_, value) => !value.isNull
  )

  errors.transformValues(() => new WrongJsonSink)

  val maybeJsons: KStream[Bytes, Json] = input.flatMapValues(_.asArray.getOrElse(Vector.empty))

  maybeJsons.to("exercise-drink-out")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), config.toProperties)

  streams.cleanUp()

  streams.start()

  sys.ShutdownHookThread {
    streams.close()
    streams.cleanUp()
  }

}
