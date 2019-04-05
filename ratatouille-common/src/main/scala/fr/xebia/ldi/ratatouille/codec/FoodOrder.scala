package fr.xebia.ldi.ratatouille.codec

import cats.Applicative
import fr.xebia.ldi.ratatouille.codec.Breakfast.{Meat, Pastry}
import scodec.{Attempt, Codec}
import scodec.bits._
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
trait FoodOrder

object FoodOrder {

  object FoodType extends Enumeration {
    type FoodType = Value
    val BreakfastType, LunchType, DrinkType, DinnerType = Value
  }

  import FoodType._

  implicit val attemptApplicative: Applicative[Attempt] = Worksheet.AttemptA()
  implicit val bf2Codec: Codec[Either[Meat, Vector[Pastry]]] = Worksheet.CodecB()

  implicit val childTypeCodec : Codec[FoodType] = scodec.codecs.enumerated(uint8, FoodType)

  implicit val codec : Codec[FoodOrder] = scodec.codecs.discriminated[FoodOrder].by(Codec[FoodType])
    .typecase(BreakfastType, Codec[Breakfast])
    .typecase(LunchType, Codec[Lunch])
    .typecase(DrinkType, Codec[Drink])
    .typecase(DinnerType, Codec[Dinner])
}