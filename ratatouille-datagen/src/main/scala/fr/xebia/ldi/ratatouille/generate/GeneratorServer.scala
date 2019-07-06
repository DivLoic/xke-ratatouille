package fr.xebia.ldi.ratatouille.generate

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.kafka.ProducerMessage.Message
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import fr.xebia.ldi.ratatouille.generate.actor.{ActorGenA, ActorGenB, ActorGenC, ActorGenD}
import fr.xebia.ldi.ratatouille.generate.http.Routing
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.slf4j.LoggerFactory
import pureconfig.loadConfig
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by loicmdivad.
  */
object GeneratorServer extends App with ConfigurableApp with Routing {

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

  implicit val exercisesPool: GeneratorPool = new GeneratorPool(
    ActorGenA(),
    ActorGenB(),
    ActorGenC(),
    ActorGenD(),
  )

  sys.addShutdownHook {
    logger error "Closing materializer and actor system."
    materializer.shutdown()
    system.terminate()
  }

  (for {

    kafkaClientConfig <- loadConfig[KafkaClientConfig]("akka.kafka.producer.kafka-clients")

    appConfig <- loadConfig[AppConfig]("xke")

  } yield {

    topicsCreation(kafkaClientConfig, appConfig)

    logger info s"Binding Generator server to: ${appConfig.httpServer.host}:${appConfig.httpServer.port}"

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
