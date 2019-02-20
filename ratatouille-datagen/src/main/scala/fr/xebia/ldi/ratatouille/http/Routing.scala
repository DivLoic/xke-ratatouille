package fr.xebia.ldi.ratatouille.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import fr.xebia.ldi.ratatouille.http.route.{StartExercise, Status, StopExercise}

/**
  * Created by loicmdivad.
  */
trait Routing extends CorsSupport
  with Status
  with StartExercise
  with StopExercise {

  val routes: Route =
    pathPrefix("api") {
      corsHandler {
        statusRoute ~
          startingRoute ~
          stopingRoute
      }
    }

}
