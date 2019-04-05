package fr.xebia.ldi.ratatouille.common.serde

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import org.apache.kafka.common.serialization.{Serde, Serdes}

/**
  * Created by loicmdivad.
  */
object FoodOrderSerde {

    def foodSerde: Serde[FoodOrder] =
      Serdes.serdeFrom(new FoodOrderSerializer, new FoodOrderDeserializer)
}
