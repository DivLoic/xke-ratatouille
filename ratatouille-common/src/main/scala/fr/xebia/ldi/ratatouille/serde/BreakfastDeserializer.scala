package fr.xebia.ldi.ratatouille.serde

import java.util

import fr.xebia.ldi.ratatouille.codec.Breakfast
import fr.xebia.ldi.ratatouille.codec.Breakfast.{Boose, Fruit, Lang, Pastry}
import org.apache.kafka.common.serialization.Deserializer
import scodec.{Attempt, Codec, DecodeResult, Err, SizeBound}
import scodec.codecs._
import scodec.codecs.implicits._
import scodec.bits.BitVector

/**
  * Created by loicmdivad.
  */
class BreakfastDeserializer extends Deserializer[Breakfast] {

  import BreakfastDeserializer.breakfastEvidence

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): Breakfast =
    Codec.decode[Breakfast](BitVector(data)).require.value

  override def close(): Unit = {}
}


object BreakfastDeserializer {

  implicit private[serde] lazy val breakfastEvidence: Codec[Breakfast] = new Codec[Breakfast] {

    case class CodecBreakfast(lang: Lang, drink: Boose, fruit: Fruit, dishes: Vector[Pastry] = Vector.empty)

    override def encode(value: Breakfast): Attempt[BitVector] =
      Attempt.failure(Err("encoding use inside of the deserializer"))

    override def sizeBound: SizeBound = byte.sizeBound

    override def decode(bits: BitVector): Attempt[DecodeResult[Breakfast]] =
      Codec.decode[CodecBreakfast](bits).map { result =>
        result.map { cb =>
          Breakfast(cb.lang, cb.drink, cb.fruit, Right(cb.dishes))
        }
      }
  }
}