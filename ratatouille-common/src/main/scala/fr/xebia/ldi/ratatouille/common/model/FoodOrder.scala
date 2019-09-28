package fr.xebia.ldi.ratatouille.common.model

import com.sksamuel.avro4s.RecordFormat
import fr.xebia.ldi.ratatouille.common.codec.Buggy
import org.apache.avro.generic.GenericRecord
import scodec.Codec
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
trait FoodOrder {

  def toAvro: GenericRecord
}

object FoodOrder {

  object FoodType extends Enumeration {
    type FoodType = Value
    val BreakfastType, LunchType, DrinkType, DinnerType = Value
  }

  import FoodType._
  import fr.xebia.ldi.ratatouille.common.codec.symbolCodec
  import fr.xebia.ldi.ratatouille.common.codec.breakfastDishCodec

  implicit val foodTypeCodec : Codec[FoodType] = scodec.codecs.enumerated(uint8, FoodType)

  implicit val foodCodec : Codec[FoodOrder] = scodec.codecs.discriminated[FoodOrder].by(Codec[FoodType])
    .typecase(BreakfastType, Codec[Breakfast])
    .typecase(LunchType, Codec[Lunch])
    .typecase(DrinkType, Codec[Drink])
    .typecase(DinnerType, Codec[Dinner])

  private[common] lazy val buggyFoodCodec = scodec.codecs.discriminated[FoodOrder].by(Codec[FoodType])
    .typecase(BreakfastType, Codec[Breakfast])
    .typecase(LunchType, Codec[Lunch])
    .typecase(DrinkType, Codec[Drink](/** -> */Buggy.drinkEvidence /** <- delete this to fix the codec */))
    .typecase(DinnerType, Codec[Dinner](/** -> */Buggy.dinnerEvidence /** <- delete this to fix the codec */))

  implicit lazy val BreakfastFormat: RecordFormat[Breakfast] = RecordFormat[Breakfast]
  implicit lazy val LunchFormat: RecordFormat[Lunch] = RecordFormat[Lunch]
  implicit lazy val DrinkFormat: RecordFormat[Drink] = RecordFormat[Drink]
  implicit lazy val DinnerFormat: RecordFormat[Dinner] = RecordFormat[Dinner]
}
