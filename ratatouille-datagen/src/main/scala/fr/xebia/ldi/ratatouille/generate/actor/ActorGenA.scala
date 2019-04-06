package fr.xebia.ldi.ratatouille.generate.actor

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import fr.xebia.ldi.ratatouille.common.model.Breakfast
import fr.xebia.ldi.ratatouille.generate.GeneratorPool.InputTopic
import fr.xebia.ldi.ratatouille.generate.actor.ActorGen.Worker
import fr.xebia.ldi.ratatouille.generate.actor.ActorGenA.ActorGenAWorker
import fr.xebia.ldi.ratatouille.generate.actor.Event._
import fr.xebia.ldi.ratatouille.generate.gen.BreakfastGen
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters.default
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by loicmdivad.
  */
case class ActorGenA()(implicit
                       val producer: ActorRef,
                       val actorSystem: ActorSystem,
                       val dispatcher: ExecutionContext) extends ActorGen {

  override def name: String = ActorGenA.name

  override protected val worker: ActorRef =
    actorSystem.actorOf(Props.apply(classOf[ActorGenAWorker], producer, actorSystem))
}

object ActorGenA {

  val name = "breakfast"

  private case class ActorGenAWorker(producer: ActorRef)
                                    (implicit val actorSystem: ActorSystem) extends Actor {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    private var status: State = Standby

    private val fr = actorSystem.actorOf(Props.apply(classOf[FrenchWorker], self))
    private val en = actorSystem.actorOf(Props.apply(classOf[EnglishWorker], self))

    override def receive: Receive = {
      case Status => logger info s"$name worker: receive a status call"
        sender ! status

      case Start => logger info s"$name worker: receive start call"
        status = Running
        fr ! Start
        en ! Start
        sender ! Done

      case Stop => logger info s"$name worker: receive stop call"
        status = Standby
        fr ! Stop
        en ! Stop
        sender ! Done

      case message@Message(_, NotUsed) => status match {
        case Running => producer ! message
        case _ => logger warn s"$name worker: receive a send (from ${sender().path}) call status but status is Standby"
      }

      case Done =>

      case _ => logger warn s"$name worker: receive something unknown"
    }
  }

  private case class FrenchWorker(parent: ActorRef) extends Worker[Any, Breakfast] with BreakfastGen {

    override protected val title: String = "Breakfast [FR] worker"

    override def coldStart: FiniteDuration = Duration.Zero

    override def produce: Message[Any, Breakfast, NotUsed] = {
      val breakfast = generateFr.pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Breakfast](InputTopic, breakfast)
      Message[Any, Breakfast, NotUsed](record, NotUsed)
    }
  }

  private case class EnglishWorker(parent: ActorRef) extends Worker[Any, Breakfast] with BreakfastGen {

    override protected val title: String = "Breakfast [ER] worker"

    override def coldStart: FiniteDuration = 10 seconds

    override def delay: FiniteDuration = Gen.choose(2 seconds, 3 seconds).pureApply(default, Seed.random())

    override def produce: Message[Any, Breakfast, NotUsed] = {
      val breakfast = generateEn.pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Breakfast](InputTopic, breakfast)
      Message[Any, Breakfast, NotUsed](record, NotUsed)
    }
  }
}
