package fr.xebia.ldi.ratatouille.common.codec

import java.time.ZoneId

import fr.xebia.ldi.ratatouille.common.model._
import fr.xebia.ldi.ratatouille.common.model.Breakfast.{Fruit, Lang, Liquid, Pastry}
import fr.xebia.ldi.ratatouille.common.model.Drink.Wine
import fr.xebia.ldi.ratatouille.common.model.Lunch.{MainDish, Starter}
import scodec.Codec._
import scodec.bits.BitVector
import scodec.{Attempt, Codec, DecodeResult, Err, SizeBound}
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
private[common] object Buggy {

  trait BuggyCodec[T <: FoodOrder] extends Codec[T] {

    def encode(value: T): Attempt[BitVector] =
      Attempt.failure(Err("The buggy implementation is restricted to deserializer, do not try to serialise with it!"))

    def sizeBound: SizeBound = scodec.codecs.byte.sizeBound
  }

  lazy val breakfastEvidence: BuggyCodec[Breakfast] = new BuggyCodec[Breakfast] {

    private case class CodecBreakfast(lang: Lang,
                                      liquid: Liquid,
                                      fruit: Fruit,
                                      dishes: Vector[Pastry] = Vector.empty)

    override def decode(bits: BitVector): Attempt[DecodeResult[Breakfast]] =
      Codec.decode[CodecBreakfast](bits).map { result =>
        result.map { cb =>
          Breakfast(cb.lang, cb.liquid, cb.fruit, Right(cb.dishes))
        }
      }
  }

  lazy val lunchEvidence: BuggyCodec[Lunch] = (bits: BitVector) => decode[Lunch](bits)
    .flatMap { cmd =>
      cmd.value.`type` match {
        case MainDish() | Starter() => Attempt.successful(cmd)
        case _ => Attempt.failure(Err(s"Unknown currency: Dessertù&ù#@!*£%ù¨"))
      }
    }

  lazy val drinkEvidence: BuggyCodec[Drink] = (bits: BitVector) => decode[Drink](bits)
    .flatMap { cmd =>
      cmd.value.`type` match {
        case Wine() => decode[Drink](BitVector(bits.toByteArray.head +: bits.toByteArray.tail.reverse.toVector))
        case _ => Attempt.successful(cmd)
      }
    }

  lazy val dinnerEvidence: BuggyCodec[Dinner] = (bits: BitVector) => decode[Dinner](bits)
    .flatMap { cmd =>
      cmd.value.moment.zone match {
        case zone if zone.toString equals ZoneId.SHORT_IDS.get("AET") =>
          Attempt.failure(Err(s"Failed to decode dinner: ${bits.toHex}" +
            cstring.encode(ZoneId.SHORT_IDS.get("AET")).require.toHex))
        case _ => Attempt.successful(cmd)
      }
    }
}
