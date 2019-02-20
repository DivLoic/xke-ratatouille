package fr.xebia.ldi.ratatouille.serde

import java.nio.ByteBuffer
import java.util

import io.circe.Json
import io.circe.jawn.parseByteBuffer
import org.apache.kafka.common.serialization.Deserializer


/**
  * Created by loicmdivad.
  */
class CirceDeserializer extends Deserializer[Json]{

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): Json = {
    parseByteBuffer(ByteBuffer.wrap(data)) match {
      case Left(err) => throw err.underlying // buggy implementation
      case Right(value) => value
    }
  }

  override def close(): Unit = {}
}
