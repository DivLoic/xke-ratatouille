package fr.xebia.ldi.ratatouille.common.serde

import java.util

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import org.apache.kafka.common.serialization.Serializer
import scodec.Codec

/**
  * Created by loicmdivad.
  */
class FoodOrderSerializer extends Serializer[FoodOrder] {

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: FoodOrder): Array[Byte] =
    Codec.encode[FoodOrder](data).map(_.toByteArray).require

  override def close(): Unit = {}
}
