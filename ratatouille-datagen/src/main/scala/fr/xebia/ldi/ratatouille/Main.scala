package fr.xebia.ldi.ratatouille

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import fr.xebia.ldi.ratatouille.model.Breakfast
import fr.xebia.ldi.ratatouille.exercice.KClient._
import fr.xebia.ldi.ratatouille.exercice.{ExerciseOne, ExerciseThree, ExerciseTwo, KClient}
import fr.xebia.ldi.ratatouille.http.Routing
import io.circe.Json
import org.apache.kafka.common.utils.Bytes
import org.slf4j.LoggerFactory
import pureconfig.error.ConfigReaderFailures
import pureconfig.loadConfig

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by loicmdivad.
  */
object Main extends App with ConfigurableApp with Routing {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("ratatouille")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val stringProducer = KClient.producer[String, String].run()
  val bytesProducer = KClient.producer[Bytes, Bytes].run()
  val breakfasrProducer = KClient.producer[Bytes, Breakfast].run()
  val drinksProducer = KClient.producer[String, Json].run()

  implicit val exercisesPool: ExercisesPool = new ExercisesPool(
    Vector(
      ExerciseOne(breakfasrProducer),
      ExerciseTwo(stringProducer),
      ExerciseThree(drinksProducer)
    )
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
