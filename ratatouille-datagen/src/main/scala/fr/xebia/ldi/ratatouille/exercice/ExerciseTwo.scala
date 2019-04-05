package fr.xebia.ldi.ratatouille.exercice

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.kafka.ProducerMessage.Message
import fr.xebia.ldi.ratatouille.model.Lunch
import fr.xebia.ldi.ratatouille.exercice.Event._
import fr.xebia.ldi.ratatouille.exercice.Exercise.ExerciseWorker
import fr.xebia.ldi.ratatouille.exercice.ExerciseTwo.ExerciseTwoWorker
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalacheck.Gen.oneOf
import org.scalacheck.{Arbitrary, Gen}
import purecsv.safe._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by loicmdivad.
  */
case class ExerciseTwo(producer: ActorRef)
                      (implicit
                       val actorSystem: ActorSystem,
                       val dispatcher: ExecutionContext) extends Exercise {

  override def name: String = ExerciseTwo.name

  override val worker: ActorRef = actorSystem.actorOf(Props.apply(classOf[ExerciseTwoWorker], producer))

}

object ExerciseTwo {

  val name = "lunch"
  val topic = s"exercise-$name"

  implicit lazy val ArbitraryDish: Arbitrary[Lunch] = Arbitrary(oneOf(Lunch.menu))

  private case class ExerciseTwoWorker(producer: ActorRef) extends ExerciseWorker[String, String] {

    private var status: State = Standby

    def generate: Gen[String] = for {
      seperator <- Gen.frequency((8, ","), (1, ";"), (1, "|"))
      dish <- ArbitraryDish.arbitrary
    } yield dish.toCSV(seperator).filterNot(_ equals '"')

    override def produce: Message[String, String, NotUsed] = {
      val record = new ProducerRecord[String, String](topic, generate.sample.getOrElse(Lunch.menu(-3).toCSV(",")))
      Message[String, String, NotUsed](record, NotUsed)
    }

    override def receive: Receive = {
      case Status => logger info "receive status call"
        sender ! status

      case Start => logger info "receive start call"
        status = Running
        context.system.scheduler.scheduleOnce(1 second, self, Send)(context.dispatcher)
        sender ! Done

      case Stop => logger info "receive stop call"
        status = Standby
        sender ! Done

      case Send => status match {
        case Running =>
          producer ! produce
          context.system.scheduler.scheduleOnce(delay, self, Send)(context.dispatcher)
        case _ => logger warn "receive a send call status but status is not running "
      }

      case _ => logger warn "receive something unknown"
    }
  }

}
