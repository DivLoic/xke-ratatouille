package fr.xebia.ldi.ratatouille.common.serde

import java.util

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import org.apache.kafka.common.serialization.Deserializer
import scodec.Codec
import scodec.bits.BitVector

/**
  * Created by loicmdivad.
  */
class FoodOrderDeserializer extends Deserializer[FoodOrder] {

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): FoodOrder =
    // always use the buggy impl of the food codec
    // the food codec has to be fixed during the demo
    Codec.decode[FoodOrder](BitVector(data))(FoodOrder.buggyFoodCodec).require.value

  override def close(): Unit = {}
}
