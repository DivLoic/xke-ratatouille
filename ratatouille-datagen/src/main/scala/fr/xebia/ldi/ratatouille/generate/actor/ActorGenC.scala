package fr.xebia.ldi.ratatouille.generate.actor

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import fr.xebia.ldi.ratatouille.common.model.Drink
import fr.xebia.ldi.ratatouille.common.model.Drink.{Champagne, DrinkType, Rhum, Water, Whisky, Wine}
import fr.xebia.ldi.ratatouille.generate.GeneratorPool.InputTopic
import fr.xebia.ldi.ratatouille.generate.actor.ActorGen.Worker
import fr.xebia.ldi.ratatouille.generate.actor.ActorGenC.ActorGenCWorker
import fr.xebia.ldi.ratatouille.generate.actor.Event._
import fr.xebia.ldi.ratatouille.generate.gen.DrinkGen
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen.Parameters.default
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by loicmdivad.
  */
case class ActorGenC()
                    (implicit
                     val producer: ActorRef,
                     val actorSystem: ActorSystem,
                     val dispatcher: ExecutionContext) extends ActorGen {

  override def name: String = ActorGenC.name

  override protected val worker: ActorRef =
    actorSystem.actorOf(Props.apply(classOf[ActorGenCWorker], producer, actorSystem))
}

object ActorGenC {

  val name = "drink"

  private case class ActorGenCWorker(producer: ActorRef)
                                    (implicit val actorSystem: ActorSystem) extends Actor {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    private val workers = Vector(
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], Wine, self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], Rhum, self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], Water, self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], Whisky, self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], Champagne, self))
    )

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

  private case class ActorGenCSubWorker(`type`: DrinkType, parent: ActorRef) extends Worker[Any, Drink] with DrinkGen {

    override protected val title: String = s"Drink sub worker n°${`type`}"

    override def coldStart: FiniteDuration = `type` match {
      case Wine => Duration.Zero
      case _ => 8 seconds
    }

    override def produce: Message[Any, Drink, NotUsed] = {
      val lunch = generate(`type`).pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Drink](InputTopic, lunch)
      Message[Any, Drink, NotUsed](record, NotUsed)
    }
  }
}
