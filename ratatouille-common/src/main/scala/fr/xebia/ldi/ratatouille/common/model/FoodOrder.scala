package fr.xebia.ldi.ratatouille.common.model

import fr.xebia.ldi.ratatouille.common.codec.Buggy
import scodec.Codec
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
  import fr.xebia.ldi.ratatouille.common.codec.breakfastDishCodec

  implicit val foodTypeCodec : Codec[FoodType] = scodec.codecs.enumerated(uint8, FoodType)

  implicit val foodCodec : Codec[FoodOrder] = scodec.codecs.discriminated[FoodOrder].by(Codec[FoodType])
    .typecase(BreakfastType, Codec[Breakfast])
    .typecase(LunchType, Codec[Lunch])
    .typecase(DrinkType, Codec[Drink])
    .typecase(DinnerType, Codec[Dinner])

  private[common] lazy val buggyFoodCodec = scodec.codecs.discriminated[FoodOrder].by(Codec[FoodType])
    .typecase(BreakfastType, Codec[Breakfast](Buggy.breakfastEvidence))
    .typecase(LunchType, Codec[Lunch])
    .typecase(DrinkType, Codec[Drink])
    .typecase(DinnerType, Codec[Dinner])


}