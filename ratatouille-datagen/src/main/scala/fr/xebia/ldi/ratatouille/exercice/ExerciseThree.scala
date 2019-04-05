package fr.xebia.ldi.ratatouille.exercice

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import akka.pattern.ask
import akka.util.Timeout
import fr.xebia.ldi.ratatouille.codec.{Drink, BeverageType}
import fr.xebia.ldi.ratatouille.codec.Drink.{BeverageCommand, IncorrectBeverageCommand}
import fr.xebia.ldi.ratatouille.exercice.Event._
import fr.xebia.ldi.ratatouille.exercice.Exercise.ExerciseWorker
import fr.xebia.ldi.ratatouille.exercice.ExerciseThree.ExerciseThreeWorker
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen.{Parameters, frequency, listOfN, oneOf}
import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, Gen}
import org.slf4j.{Logger, LoggerFactory}
import cats.implicits._
import Drink.beverageMonoid

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by loicmdivad.
  */
case class ExerciseThree(producer: ActorRef)
                        (implicit
                         val actorSystem: ActorSystem,
                         val dispatcher: ExecutionContext) extends Exercise {

  override def name: String = ExerciseThree.name

  override protected val worker: ActorRef =
    actorSystem.actorOf(Props.apply(classOf[ExerciseThreeWorker], producer, actorSystem))
}

object ExerciseThree {

  val foo: BeverageCommand = BeverageCommand(Vector.empty) |+| BeverageCommand(Vector.empty)


  val name = "drink"
  val topic = s"exercise-$name"

  private case class ExerciseThreeWorker(producer: ActorRef)
                                        (implicit val actorSystem: ActorSystem) extends ExerciseWorker[Json, Json] {

    private implicit val dispatcher: ExecutionContextExecutor = context.dispatcher

    private implicit val timeout: Timeout = Timeout(5 seconds)

    private var status: State = Standby

    private val subWorkers = Vector(
      actorSystem.actorOf(Props.apply(classOf[WineWorker])),
      actorSystem.actorOf(Props.apply(classOf[WhiskyWorker])),
      actorSystem.actorOf(Props.apply(classOf[ChampagneWorker]))
    )

    def produce2(value: Json): Message[Json, Json, NotUsed] =
      Message(new ProducerRecord("exercise-drink", value), NotUsed)

    override def receive: Receive = {
      case Status => logger info "receive status call"
        sender ! status

      case Start => logger info "receive start call"
        status = Running
        context.system.scheduler.scheduleOnce(1 second, self, Send)
        sender ! Done

      case Stop => logger info "receive stop call"
        status = Standby
        sender ! Done

      case Send => status match {
        case Running =>

          val c: Future[BeverageCommand] = subWorkers
            .map(_ ? Send)
            .map(_.map {
              case cmd: BeverageCommand => cmd
              case _ => IncorrectBeverageCommand
            })

            .traverse[Future, BeverageCommand] {
            _.recover {
              case _ => IncorrectBeverageCommand
            }

          }.map(cmd => cmd.reduce(_ |+| _))

          val triedResult = Try(Await.result(c, timeout.duration))

          triedResult match {
            case Failure(_) | Success(IncorrectBeverageCommand) =>
            case Success(result) => producer ! produce2(result.beverages.asJson)
          }

          context.system.scheduler.scheduleOnce(delay, self, Send)(context.dispatcher)
        case _ => logger warn "receive a send call status but status is not running "

      }

    }

    override def produce: Message[Json, Json, NotUsed] = ???
  }

  trait ExerciseThreeSubWorker extends Actor {
    protected val logger: Logger = LoggerFactory.getLogger(getClass)
  }

  private case class WineWorker() extends ExerciseThreeSubWorker {

    private val genGlasses = Arbitrary(listOfN(2, oneOf(Drink.wines))).arbitrary

    private val genBottle = Arbitrary(listOfN(1, oneOf(Drink.wines))).arbitrary
      .map(_.map(_.copy(quantity = 75)))

    override def receive: Receive = {
      case Send => sender() ! frequency[Gen[List[Drink]]]((2, genGlasses), (1, genBottle), (3, Gen.fail)).sample
        .map(_.pureApply(Parameters.default, Seed.random).toVector)
        .map(BeverageCommand)
        .getOrElse(IncorrectBeverageCommand)

      case _ => logger warn "receive something unknown"
    }
  }

  private case class ChampagneWorker() extends ExerciseThreeSubWorker {

    private val failed = (8, Gen.fail[Drink])
    private val genarated = (2, Arbitrary(oneOf(Drink.champagnes)).arbitrary)

    override def receive: Receive = {
      case Send => sender() ! frequency[Drink](genarated, failed).sample
        .map(drink => drink.pure[Vector])
        .map(BeverageCommand)
        .getOrElse(IncorrectBeverageCommand)

      case _ => logger warn "receive something unknown"
    }
  }

  private case class RhumWorker() extends ExerciseThreeSubWorker {

    override def receive: Receive = {
      case Send => sender() ! BeverageCommand(Vector(Drink.tipunch))
    }
  }

  private case class WaterWorker() extends ExerciseThreeSubWorker {

    override def receive: Receive = {
      case Send => sender() ! BeverageCommand(Vector(Drink("Sanpellegrino", BeverageType.Water, 100, None)))
    }
  }

  private case class WhiskyWorker() extends ExerciseThreeSubWorker {

    val min = 0
    val max = 4
    val gen: Gen[Vector[Drink]] = Gen.choose(min, max).flatMap(listOfN(_, oneOf(Drink.whiskies)).map(_.toVector))

    override def receive: Receive = {
      case Send => sender() ! gen.sample
        .map(BeverageCommand)
        .getOrElse(IncorrectBeverageCommand)

      case _ => logger warn "receive something unknown"
    }
  }

}