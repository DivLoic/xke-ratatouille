package fr.xebia.ldi

/**
  * Created by loicmdivad.
  */
package object ratatouille {

  case class GeneralConfig(httpServer: HttpServer)

  case class HttpServer(host: String, port: Int)

}
