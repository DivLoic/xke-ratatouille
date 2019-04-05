package fr.xebia.ldi.ratatouille

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.kafka.ProducerMessage.Message
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import fr.xebia.ldi.ratatouille.exercice.ExerciseOne
import fr.xebia.ldi.ratatouille.http.Routing
import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.slf4j.LoggerFactory
import pureconfig.loadConfig

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by loicmdivad.
  */
object Main extends App with ConfigurableApp with Routing {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("ratatouille")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val producerConfig: ProducerSettings[Bytes, FoodOrder] =
    ProducerSettings(system, Serdes.Bytes().serializer(), FoodOrderSerde.foodSerde.serializer())

  implicit val actorProducer: ActorRef =
    Source.actorRef[Message[Bytes, FoodOrder, NotUsed]](10, OverflowStrategy.dropBuffer)
    .via(Producer.flexiFlow(producerConfig))
    .to(Sink.ignore)
    .run()

  implicit val exercisesPool: ExercisesPool = new ExercisesPool(
    ExerciseOne()
  )

  sys.addShutdownHook {
    materializer.shutdown()
    system.terminate()
  }

  (for {

    kafkaClientConfig <- loadConfig[KafkaClientConfig]("akka.kafka.producer.kafka-clients")

    appConfig <- loadConfig[AppConfig]("xke")

  } yield {

    topicsCreation(kafkaClientConfig, appConfig)

    Http().bindAndHandle(
      routes,
      appConfig.httpServer.host,
      appConfig.httpServer.port
    )

  }).left.map { failures =>
      failures.toList.foreach { failure =>
        logger error failure.description
      }
      sys.exit()
  }
}
