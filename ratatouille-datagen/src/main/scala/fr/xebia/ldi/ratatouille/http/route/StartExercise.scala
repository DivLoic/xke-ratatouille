package fr.xebia.ldi.ratatouille.http.route

import akka.http.scaladsl.server.Directives.{complete, get, pathEndOrSingleSlash, pathPrefix}
import akka.http.scaladsl.server.Route
import fr.xebia.ldi.ratatouille.http.{Protocol, Service}
import akka.http.scaladsl.server.Directives._

/**
  * Created by loicmdivad.
  */
trait StartExercise extends Protocol with Service {

  val startingRoute: Route = pathPrefix("start") {
    pathEndOrSingleSlash {
      get {
        complete(getStatus)
      }
    }
  } ~ pathPrefix("start" / Segment) {
    exerciseName =>
      pathEndOrSingleSlash {
        get {
          complete(startOne(exerciseName))
        }
      }
  }
}
