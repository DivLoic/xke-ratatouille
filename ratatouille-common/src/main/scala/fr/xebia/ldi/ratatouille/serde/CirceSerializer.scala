package fr.xebia.ldi.ratatouille.serde

import java.nio.ByteBuffer
import java.nio.charset.{Charset, StandardCharsets}
import java.util

import io.circe.{Json, _}
import org.apache.kafka.common.serialization.Serializer
import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters
import org.scalacheck.rng.Seed

/**
  * Created by loicmdivad.
  * this is a buggy implementation of Serializer for json
  * [{},{}] vs {}{} vs }{}{
  */
class CirceSerializer extends Serializer[Json] {

  val sep: Array[Byte] = "\n".getBytes(Charset.forName("UTF-8"))

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Json): Array[Byte] = {

    val (correct, wrong, fatal) = encode(data)

    // /!\ buggy implementation, fail 1 time over ten /!\
    Gen.frequency((6, correct), (2, wrong), (2, fatal)).pureApply(Parameters.default, Seed.random())
  }

  override def close(): Unit = {}

  private def encode(data: Json): (Array[Byte], Array[Byte], Array[Byte]) =
    (print(data), printEach(data), printErr(data))

  private def print(json: Json): Array[Byte] =
    delFooter(Printer.noSpaces.prettyByteBuffer(json, StandardCharsets.UTF_8))

  private def printEach(data: Json): Array[Byte] =
    data.asArray.map(_.flatMap(print(_) ++ sep).toVector).getOrElse(Vector.empty[Byte]).toArray

  private def printErr(data: Json): Array[Byte] = Array.empty[Byte]

  private def delFooter(bytes: ByteBuffer): Array[Byte] =
    bytes.array().reverse.dropWhile(_ == 0x00).reverse
}