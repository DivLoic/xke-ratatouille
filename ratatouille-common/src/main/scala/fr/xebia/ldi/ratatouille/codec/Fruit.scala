package fr.xebia.ldi.ratatouille.codec

import scodec.codecs.{Discriminator, _}


/**
  * Created by loicmdivad.
  */
sealed trait Fruit

object Fruit {

  case class Kiwi() extends Fruit
  case class Banana() extends Fruit
  case class Tomato() extends Fruit
  case class Pineapple() extends Fruit
  case class Watermelon() extends Fruit

  implicit val discriminated: Discriminated[Fruit, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

  object Kiwi {
    private val kiwiByte = BreakfastModel(0xFB.toByte)
    implicit val discriminator: Discriminator[Fruit, Kiwi, BreakfastModel] = Discriminator(kiwiByte)
  }

  object Banana {
    private val bananaByte = BreakfastModel(0xFC.toByte)
    implicit val discriminator: Discriminator[Fruit, Banana, BreakfastModel] = Discriminator(bananaByte)
  }

  object Tomato {
    private val tomatoByte = BreakfastModel(0xFD.toByte)
    implicit val discriminator: Discriminator[Fruit, Tomato, BreakfastModel] = Discriminator(tomatoByte)
  }

  object Pineapple {
    private val pineAppleByte = BreakfastModel(0xFE.toByte)
    implicit val discriminator: Discriminator[Fruit, Pineapple, BreakfastModel] = Discriminator(pineAppleByte)
  }

  object Watermelon {
    private val watermelonByte = BreakfastModel(0xFF.toByte)
    implicit val discriminator: Discriminator[Fruit, Watermelon, BreakfastModel] = Discriminator(watermelonByte)
  }
}