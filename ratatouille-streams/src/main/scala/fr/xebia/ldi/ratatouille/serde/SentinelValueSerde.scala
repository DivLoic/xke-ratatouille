package fr.xebia.ldi.ratatouille.serde

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import fr.xebia.ldi.ratatouille.common.serde.{FoodOrderDeserializer, FoodOrderSerializer}
import org.apache.kafka.common.serialization.{Serde, Serdes}

import scala.util.Try

/**
  * Created by loicmdivad.
  */
object SentinelValueSerde {

  case object FoodOrderErr extends FoodOrder

  def serde: Serde[FoodOrder] = Serdes.serdeFrom(new FoodOrderSerializer, new SentinelValueDeserializer)

  class SentinelValueDeserializer extends FoodOrderDeserializer {

    override def deserialize(topic: String, data: Array[Byte]): FoodOrder =
      Try(super.deserialize(topic, data)).getOrElse(FoodOrderErr)

  }
}
