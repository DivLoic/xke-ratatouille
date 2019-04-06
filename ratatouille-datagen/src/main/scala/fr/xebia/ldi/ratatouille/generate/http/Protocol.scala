package fr.xebia.ldi.ratatouille.generate.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import fr.xebia.ldi.ratatouille.generate.http.model.{Command, Exercise, Status}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContext

/**
  * Created by loicmdivad.
  */
trait Protocol extends DefaultJsonProtocol with SprayJsonSupport with Conf {
  protected implicit def executionContext: ExecutionContext
  protected implicit def materializer: ActorMaterializer
  //protected def logger: LoggingAdapter


  implicit val exerciseFormat: RootJsonFormat[Exercise] = jsonFormat2(Exercise)
  implicit val statusFormat: RootJsonFormat[Status] = jsonFormat3(Status)
  implicit val cmdFormat: RootJsonFormat[Command] = jsonFormat2(Command)


}
