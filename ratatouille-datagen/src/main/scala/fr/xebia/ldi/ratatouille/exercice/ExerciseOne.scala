package fr.xebia.ldi.ratatouille.exercice

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import akka.pattern.ask
import akka.util.Timeout
import fr.xebia.ldi.ratatouille.model.Breakfast
import fr.xebia.ldi.ratatouille.model.Lang.{EN, FR}
import fr.xebia.ldi.ratatouille.exercice.Event._
import fr.xebia.ldi.ratatouille.exercice.Exercise.ExerciseWorker
import fr.xebia.ldi.ratatouille.exercice.ExerciseOne.ExerciseOneWorker
import fr.xebia.ldi.ratatouille.exercice.Generator.GeneratorOne.generate
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters.default
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}

/**
  * Created by loicmdivad.
  */
case class ExerciseOne(producer: ActorRef)
                      (implicit
                       val actorSystem: ActorSystem,
                       val dispatcher: ExecutionContext) extends Exercise {

  override def name: String = ExerciseOne.name

  override protected val worker: ActorRef =
    actorSystem.actorOf(Props.apply(classOf[ExerciseOneWorker], producer, actorSystem))
}

object ExerciseOne {

  val name = "breakfast"
  val topic = s"exercise-$name"

  private case class ExerciseOneWorker(producer: ActorRef)
                                      (implicit val actorSystem: ActorSystem) extends Actor {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    private var status: State = Standby

    private val fr = actorSystem.actorOf(Props.apply(classOf[FrenchWorker], self))
    private val en = actorSystem.actorOf(Props.apply(classOf[EnglishWorker], self))

    override def receive: Receive = {
      case Status => sender ! status

      case Start => logger info "receive start call"
        status = Running
        fr ! Start
        en ! Start
        sender ! Done

      case Stop => logger info "receive stop call"
        status = Standby
        fr ! Stop
        en ! Stop
        sender ! Done

      case message@Message(_, NotUsed) => status match {
        case Running => producer ! message
        case _ => logger warn "receive a send call status but status is not running "
      }

      case Done =>
      case _ => logger warn "receive something unknown"
    }
  }

  private case class FrenchWorker(parent: ActorRef) extends SubWorker(parent = parent) {

    override protected val title: String = "french sub worker"

    override def coldStart: FiniteDuration = Duration.Zero

    override def produce: Message[Any, Breakfast, NotUsed] = {
      val breakfast = generate(FR()).pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Breakfast](topic, breakfast)
      Message[Any, Breakfast, NotUsed](record, NotUsed)
    }
  }

  private case class EnglishWorker(parent: ActorRef) extends SubWorker(parent = parent) {

    override protected val title: String = "english sub worker"

    override def coldStart: FiniteDuration = 8 seconds

    override def delay: FiniteDuration = Gen.choose(2 seconds, 3 seconds).pureApply(default, Seed.random())

    override def produce: Message[Any, Breakfast, NotUsed] = {
      val breakfast = generate(EN()).pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Breakfast](topic, breakfast)
      Message[Any, Breakfast, NotUsed](record, NotUsed)
    }
  }

  private abstract class SubWorker(parent: ActorRef) extends ExerciseWorker[Any, Breakfast] {

    protected val title: String

    implicit private val executionContext: ExecutionContextExecutor = context.dispatcher
    implicit protected val timeout: Timeout = Timeout(1 seconds)

    def coldStart: FiniteDuration

    def askStatus(): State = Await.result((parent ? Status)
      .map { case s: State => s; case _ => Down }
      .recover { case _ => Down },
      timeout.duration
    )

    override def receive: Receive = {
      case Start => logger info s"$title -  receive start call"
        context.system.scheduler.scheduleOnce(coldStart, self, Send)(context.dispatcher)
        sender ! Done

      case Stop => logger info s"$title -  receive stop call"
        sender ! Done

      case Send => askStatus() match {
        case Running => parent ! produce
          context.system.scheduler.scheduleOnce(delay, self, Send)(context.dispatcher)

        case err => logger warn s"$title -  receive a send call status but status is not running : $err"
      }
      case Status =>
      case _ => logger warn s"$title -  receive something unknown"
    }
  }
}