package fr.xebia.ldi.ratatouille.serde

import java.util

import io.circe.Json
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}

/**
  * Created by loicmdivad.
  */
class CirceSerde extends Serde[Json] {

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[Json] = new CirceSerializer

  override def deserializer(): Deserializer[Json] = new CirceDeserializer
}

object CirceSerde {

  def serdes: Serde[Json] = Serdes.serdeFrom(new CirceSerializer, new CirceDeserializer)
}