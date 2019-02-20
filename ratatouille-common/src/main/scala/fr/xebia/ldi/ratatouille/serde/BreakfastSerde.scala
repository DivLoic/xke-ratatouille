package fr.xebia.ldi.ratatouille.serde

import java.util

import fr.xebia.ldi.ratatouille.codec._
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}

/**
  * Created by loicmdivad.
  */
class BreakfastSerde extends Serde[Breakfast] {

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[Breakfast] = new BreakfastSerializer

  override def deserializer(): Deserializer[Breakfast] = new BreakfastDeserializer
}


object BreakfastSerde {

  object Serde {

    def breakfast: Serde[Breakfast] = Serdes.serdeFrom(new BreakfastSerializer,  new BreakfastDeserializer)
  }
}