package fr.xebia.ldi.ratatouille.serdes

import io.circe.Json
import io.circe.Json.arr
import io.circe.parser.parse
import cats.syntax.applicative._
import cats.instances.vector._

/**
  * Created by loicmdivad.
  */
object JsonSerdeUtils {

  val separatedPayloadMess = "expected whitespace or eof got '{"

  def jsonRecoverFunc(data: Array[Byte]): Json =
    arr(data.toVector.toString.split("\n").flatMap(j => parse(j).map(_.pure[Vector]).getOrElse(Vector.empty[Json])):_*)
}
