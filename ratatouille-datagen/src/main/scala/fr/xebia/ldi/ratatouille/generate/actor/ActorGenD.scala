package fr.xebia.ldi.ratatouille.generate.actor

import java.time.ZoneId

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import fr.xebia.ldi.ratatouille.common.model.Dinner
import fr.xebia.ldi.ratatouille.generate.GeneratorPool.InputTopic
import fr.xebia.ldi.ratatouille.generate.actor.ActorGen.Worker
import fr.xebia.ldi.ratatouille.generate.actor.ActorGenD.ActorGenDWorker
import fr.xebia.ldi.ratatouille.generate.actor.Event._
import fr.xebia.ldi.ratatouille.generate.gen.DinnerGen
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen.Parameters.default
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by loicmdivad.
  */
case class ActorGenD()
                    (implicit
                     val producer: ActorRef,
                     val actorSystem: ActorSystem,
                     val dispatcher: ExecutionContext) extends ActorGen {

  override def name: String = ActorGenD.name

  override protected val worker: ActorRef =
    actorSystem.actorOf(Props.apply(classOf[ActorGenDWorker], producer, actorSystem))
}

object ActorGenD {

  val name = "dinner"

  private case class ActorGenDWorker(producer: ActorRef)
                                    (implicit val actorSystem: ActorSystem) extends Actor {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    private val workers = Vector[ActorRef](
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], ZoneId.of("Europe/Paris"), self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], ZoneId.of("Europe/London"), self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], ZoneId.of("Africa/Nairobi"), self)),
      actorSystem.actorOf(Props.apply(classOf[ActorGenCSubWorker], ZoneId.of("Australia/Sydney"), self))
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

  private case class ActorGenCSubWorker(zone: ZoneId, parent: ActorRef) extends Worker[Any, Dinner] with DinnerGen {

    override protected val title: String = s"Dinner sub worker - $zone"

    override def coldStart: FiniteDuration =
      if(zone equals ZoneId.of("Australia/Sydney")) 10 seconds else Duration.Zero

    override def produce: Message[Any, Dinner, NotUsed] = {
      val lunch = generate(zone).pureApply(default, Seed.random())
      val record = new ProducerRecord[Any, Dinner](InputTopic, lunch)
      Message[Any, Dinner, NotUsed](record, NotUsed)
    }
  }
}
