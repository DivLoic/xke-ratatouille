package fr.xebia.ldi.ratatouille.http.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import fr.xebia.ldi.ratatouille.http.{Protocol, Service}

/**
  * Created by loicmdivad.
  */
trait Status extends Protocol with Service {

  val statusRoute: Route = pathPrefix("status") {
    pathEndOrSingleSlash {
      get {
        complete(getStatus)
      }
    }
  }
}
