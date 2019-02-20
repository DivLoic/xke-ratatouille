package fr.xebia.ldi.ratatouille.http.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import fr.xebia.ldi.ratatouille.http.{Protocol, Service}

/**
  * Created by loicmdivad.
  */
trait StopExercise extends Protocol with Service {

  val stopingRoute: Route = pathPrefix("stop") {
    pathEndOrSingleSlash {
      get {
        complete(getStatus)
      }
    }
  } ~ pathPrefix("stop" / Segment) {
    exerciseName =>
      pathEndOrSingleSlash {
        get {
          complete(stopOne(exerciseName))
        }
      }
  }

}
