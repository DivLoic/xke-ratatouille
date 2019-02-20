package fr.xebia.ldi.ratatouille.codec

import scodec.codecs.{Discriminated, Discriminator}


/**
  * Created by loicmdivad.
  */
sealed trait Drink

object Drink {

  case class Tea() extends Drink

  case class Milk() extends Drink

  case class Beer() extends Drink

  case class Coffe() extends Drink

  case class AppleJuice() extends Drink

  case class OrangeJuice() extends Drink

  implicit val discriminated: Discriminated[Drink, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

  object Tea {
    val teaByte = BreakfastModel(0xD0.toByte)
    implicit val discriminator: Discriminator[Drink, Tea, BreakfastModel] = Discriminator(teaByte)
  }

  object Milk {
    val milkByte = BreakfastModel(0xD1.toByte)
    implicit val discriminator: Discriminator[Drink, Milk, BreakfastModel] = Discriminator(milkByte)
  }

  object Beer {
    val beerByte = BreakfastModel(0xD2.toByte)
    implicit val discriminator: Discriminator[Drink, Beer, BreakfastModel] = Discriminator(beerByte)
  }

  object Coffe {
    val coffeByte = BreakfastModel(0xD3.toByte)
    implicit val discriminator: Discriminator[Drink, Coffe, BreakfastModel] = Discriminator(coffeByte)
  }

  object OrangeJuice {
    val orangeByte = BreakfastModel(0xD4.toByte)
    implicit val discriminator: Discriminator[Drink, OrangeJuice, BreakfastModel] = Discriminator(orangeByte)
  }

  object AppleJuice {
    val appleByte = BreakfastModel(0xD5.toByte)
    implicit val discriminator: Discriminator[Drink, AppleJuice, BreakfastModel] = Discriminator(appleByte)
  }
}
