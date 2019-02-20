package fr.xebia.ldi.ratatouille.serdes

import java.nio.ByteBuffer
import java.util

import fr.xebia.ldi.ratatouille.serde.CirceSerializer
import fr.xebia.ldi.ratatouille.serdes.JsonSerdeUtils.{jsonRecoverFunc, separatedPayloadMess}
import io.circe.jawn.parseByteBuffer
import io.circe.{Json, ParsingFailure}
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes}

import scala.util.Try

/**
  * Created by loicmdivad.
  */
object JsonSerdes {

  class JsonDeserializer extends Deserializer[Json] {

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def deserialize(topic: String, data: Array[Byte]): Json =
      parseByteBuffer(ByteBuffer.wrap(data)) match {

        case Right(value) => value

        case Left(err: ParsingFailure) if err.message.startsWith(separatedPayloadMess) =>
          // From {}{}{} to [ {}, {}, {} ]
          Try(jsonRecoverFunc(data)).getOrElse(Json.Null)

        case Left(_) => Json.Null
      }

    override def close(): Unit = {}
  }

  def serdes: Serde[Json] = Serdes.serdeFrom(new CirceSerializer, new JsonDeserializer)
}
