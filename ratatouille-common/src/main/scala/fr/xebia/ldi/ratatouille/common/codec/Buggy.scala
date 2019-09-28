package fr.xebia.ldi.ratatouille.common.codec

import java.time.ZoneId

import fr.xebia.ldi.ratatouille.common.model._
import fr.xebia.ldi.ratatouille.common.model.Breakfast.{Fruit, Lang, Liquid, Pastry}
import fr.xebia.ldi.ratatouille.common.model.Drink.Wine
import fr.xebia.ldi.ratatouille.common.model.Lunch.{MainDish, Starter}
import scodec.Codec._
import scodec.bits.BitVector
import scodec.{Attempt, Codec, Err, SizeBound}
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
private[common] object Buggy {

  final case class BuggyBreakfast(lang: Lang, liquid: Liquid, fruit: Fruit, dishes: Vector[Pastry])

  trait BuggyCodec[T <: FoodOrder] extends Codec[T] {

    def encode(value: T): Attempt[BitVector] =
      Attempt.failure(Err("The buggy implementation is restricted to deserializer, do not try to serialise with it!"))

    def sizeBound: SizeBound = scodec.codecs.byte.sizeBound
  }

  lazy val breakfastEvidence: BuggyCodec[Breakfast] = (bits: BitVector) => Codec.decode[BuggyBreakfast](bits)
    .map { result =>
    result.map { cb =>
      Breakfast(cb.lang, cb.liquid, cb.fruit, Right(cb.dishes))
    }
  }

  lazy val lunchEvidence: BuggyCodec[Lunch] = (bits: BitVector) => decode[Lunch](bits)
    .flatMap { cmd =>
      cmd.value.`type` match {
        case MainDish | Starter => Attempt.successful(cmd)
        case _ => Attempt.failure(Err(s"Unknown currency: Dessertù&ù#@!*£%ù¨"))
      }
    }

  lazy val drinkEvidence: BuggyCodec[Drink] = (bits: BitVector) => decode[Drink](bits)
    .flatMap { cmd =>
      cmd.value.`type` match {
        case Wine => Attempt.successful(cmd)
        case _ => decode[Drink](BitVector(bits.toByteArray.head +: bits.toByteArray.tail.reverse.toVector))
      }
    }

  lazy val dinnerEvidence: BuggyCodec[Dinner] = (bits: BitVector) => decode[Dinner](bits)
    .flatMap { cmd =>
      cmd.value.moment.region match {
        case zone if zone.toString equals ZoneId.of("Australia/Sydney").getId =>
          Attempt.failure(Err(s"missing evidence of Discriminator[Localized[_ <: TimeZone]]"))
        case _ => Attempt.successful(cmd)
      }
    }
}
