package fr.xebia.ldi.ratatouille.exercice

import akka.NotUsed
import akka.actor.{Actor, ActorRef}
import akka.kafka.ProducerMessage.Message
import akka.pattern.ask
import akka.util.Timeout
import fr.xebia.ldi.ratatouille.exercice.Event._
import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters
import org.scalacheck.rng.Seed
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by loicmdivad.
  */
trait Exercise {

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


object Exercise {

  trait ExerciseWorker[K, V] extends Actor {

    protected val logger: Logger = LoggerFactory.getLogger(getClass)

    def delay: FiniteDuration = Gen.choose(500 milli, 1.5 second).pureApply(Parameters.default, Seed.random())

    def produce: Message[K, V, NotUsed]
  }
}