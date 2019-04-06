package fr.xebia.ldi.ratatouille.generate.actor

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import fr.xebia.ldi.ratatouille.common.model.Lunch
import fr.xebia.ldi.ratatouille.generate.GeneratorPool.InputTopic
import fr.xebia.ldi.ratatouille.generate.actor.ActorGen.Worker
import fr.xebia.ldi.ratatouille.generate.actor.ActorGenB.ActorGenBWorker
import fr.xebia.ldi.ratatouille.generate.actor.Event._
import fr.xebia.ldi.ratatouille.generate.gen.LunchGen
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen.Parameters.default
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by loicmdivad.
  */
case class ActorGenB()(implicit
                       val producer: ActorRef,
                       val actorSystem: ActorSystem,
                       val dispatcher: ExecutionContext) extends ActorGen {

  override def name: String = ActorGenB.name

  override val worker: ActorRef =
    actorSystem.actorOf(Props.apply(classOf[ActorGenBWorker], producer, actorSystem))

}

object ActorGenB {

  val name = "lunch"

  private case class ActorGenBWorker(producer: ActorRef)
                                    (implicit val actorSystem: ActorSystem) extends Actor {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    private val workers = (0 until 3).map { id =>
      actorSystem.actorOf(Props.apply(classOf[ActorGenBSubWorker], id, self))
    }

    private var status: State = Standby

    override def receive: Receive = {
      case Status => logger info s"$name worker: receive a status call"
        sender ! status

      case Start => logger info s"$name worker: receive a start call"
        status = Running
        workers foreach (_ ! Start)
        sender ! Done

      case Stop => logger info s"$name worker: receive a stop call"
        status = Standby
        workers foreach (_ ! Stop)
        sender ! Done

      case message@Message(_, NotUsed) => status match {
        case Running => producer ! message
        case _ => logger warn s"$name worker: receive a send (from ${sender().path}) call status but status is Standby"
      }

      case Done =>

      case other => logger warn s"receive something unknown $other"
    }
  }

  private case class ActorGenBSubWorker(id: Int, parent: ActorRef) extends Worker[Any, Lunch] with LunchGen {

    override protected val title: String = s"Lunch sub worker n°$id"

    override def coldStart: FiniteDuration = id seconds

    override def produce: Message[Any, Lunch, NotUsed] = {
      val lunch = generate.pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Lunch](InputTopic, lunch)
      Message[Any, Lunch, NotUsed](record, NotUsed)
    }
  }
}
