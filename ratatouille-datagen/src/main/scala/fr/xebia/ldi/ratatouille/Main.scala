package fr.xebia.ldi.ratatouille

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import fr.xebia.ldi.ratatouille.codec.Breakfast
import fr.xebia.ldi.ratatouille.exercice.KClient._
import fr.xebia.ldi.ratatouille.exercice.{ExerciseOne, ExerciseThree, ExerciseTwo, KClient}
import fr.xebia.ldi.ratatouille.http.Routing
import io.circe.Json
import org.apache.kafka.common.utils.Bytes
import org.slf4j.LoggerFactory
import pureconfig.loadConfig

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by loicmdivad.
  */
object Main extends App with Routing {

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

  loadConfig[GeneralConfig]("xke") match {
    case Left(failures) =>
      failures.toList.foreach { failure =>
        logger error failure.description
      }

      sys.exit()

    case Right(config) =>

      Http().bindAndHandle(
        routes,
        config.httpServer.host,
        config.httpServer.port
      )
  }
}
