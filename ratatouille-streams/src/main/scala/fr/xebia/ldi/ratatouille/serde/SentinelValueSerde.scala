package fr.xebia.ldi.ratatouille.serde

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import fr.xebia.ldi.ratatouille.common.serde.{FoodOrderDeserializer, FoodOrderSerializer}
import org.apache.kafka.common.serialization.{Serde, Serdes}

import scala.util.{Failure, Success, Try}

/**
  * Created by loicmdivad.
  */
object SentinelValueSerde {

  case object FoodOrderErr extends FoodOrder

  def serde: Serde[FoodOrder] = Serdes.serdeFrom(new FoodOrderSerializer, new SentinelValueDeserializer)

  class SentinelValueDeserializer extends FoodOrderDeserializer {

    override def deserialize(topic: String, data: Array[Byte]): FoodOrder =
    // demo purpose: this wont let you run step 3 & 4 simultaneously
    // Try(super.deserialize(topic, data)).getOrElse(FoodOrderErr)


      Try(super.deserialize(topic, data)) match {
        case Success(value) => value
        case Failure(err) if err.getMessage.endsWith("AET") => throw err
        case Failure(_) => FoodOrderErr
      }

  }
}
