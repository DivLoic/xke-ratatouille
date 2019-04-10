package fr.xebia.ldi.ratatouille.generate.actor

import akka.NotUsed
import akka.actor.{Actor, ActorRef}
import akka.kafka.ProducerMessage.Message
import akka.pattern.ask
import akka.util.Timeout
import fr.xebia.ldi.ratatouille.generate.actor.Event._
import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by loicmdivad.
  */
trait ActorGen {

  def name: String

  protected val worker: ActorRef
  implicit val dispatcher: ExecutionContext
  implicit protected val timeout: Timeout = Timeout(5 seconds)

  protected val logger: Logger = LoggerFactory.getLogger(getClass)

  def !(message: Any): Unit = worker ! message
  def ?(message: Any): Future[Any] = worker ? message

  def attemptStatus: Future[State] = ?(Status).map { case s: State => s case _ => Down }

  def getStatus: State = Await.result(attemptStatus.recover { case _ => Down }, timeout.duration)

  def attemptStart: Future[ActMessage] = ?(Start).map { _ => Done }

  def start: ActMessage = Await.result(attemptStart.recover { case _ => Down }, timeout.duration)

  def attemptStop: Future[ActMessage] = ?(Stop).map { _ => Done }

  def stop: ActMessage = Await.result(attemptStop.recover { case _ => Down }, timeout.duration)
}


object ActorGen {

  trait Worker[K, V] extends Actor {

    protected val title: String

    protected val parent: ActorRef

    protected var status: State = Standby

    protected val logger: Logger = LoggerFactory.getLogger(getClass)

    def coldStart: FiniteDuration

    def produce: Message[K, V, NotUsed]

    def delay: FiniteDuration = Gen.choose(500 milli, 1.5 second).pureApply(Parameters.default, Seed.random())

    override def receive: Receive = {
      case Start => logger info s"$title - receive start call"
        context.system.scheduler.scheduleOnce(coldStart, self, Send)(context.dispatcher)
        status = Running
        sender ! Done

      case Stop => logger info s"$title - receive stop call"
        status = Standby
        sender ! Done

      case Send => parent ! produce
        if(status == Running) context.system.scheduler.scheduleOnce(delay, self, Send)(context.dispatcher)

      case _ => logger warn s"$title - receive something unknown"
    }
  }
}
