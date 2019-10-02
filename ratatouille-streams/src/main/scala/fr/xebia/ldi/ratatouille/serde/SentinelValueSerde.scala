package fr.xebia.ldi.ratatouille.serde

import com.sksamuel.avro4s.RecordFormat
import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import fr.xebia.ldi.ratatouille.common.serde.{FoodOrderDeserializer, FoodOrderSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Serde, Serdes}

import scala.util.{Failure, Success, Try}

/**
  * Created by loicmdivad.
  */
object SentinelValueSerde {

  case object FoodOrderError extends FoodOrder {
    override def toAvro: GenericRecord = RecordFormat[FoodOrderError.type].to(this)
  }

  def serde: Serde[FoodOrder] = Serdes.serdeFrom(new FoodOrderSerializer, new SentinelValueDeserializer)

  class SentinelValueDeserializer extends FoodOrderDeserializer {

    override def deserialize(topic: String, data: Array[Byte]): FoodOrder =
    // demo purpose: this wont let you run step 3 & 4 simultaneously
    // Try(super.deserialize(topic, data)).getOrElse(FoodOrderErr)

      Try(super.deserialize(topic, data)) match {
        case Success(value) => value
        case Failure(err) if err.getMessage.contains("TimeZone") => throw err
        case Failure(_) => FoodOrderError
      }
  }
}
