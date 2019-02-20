package fr.xebia.ldi.ratatouille.codec

import scodec.codecs._

/**
  * Created by loicmdivad.
  */
sealed trait Pastry

object Pastry {

  case class Croissant() extends Pastry
  case class Chouquette() extends Pastry
  case class Painauchocolat() extends Pastry
  case class Painauraisins() extends Pastry

  case class Waffle() extends Pastry
  case class Pancake() extends Pastry

  implicit val discriminated: Discriminated[Pastry, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

  object Croissant {
    val croissantByte = BreakfastModel(0xA5.toByte)
    implicit val discriminator: Discriminator[Pastry, Croissant, BreakfastModel] = Discriminator(croissantByte)
  }

  object Chouquette {
    val chouquetteByte = BreakfastModel(0xB4.toByte)
    implicit val discriminator: Discriminator[Pastry, Chouquette, BreakfastModel] = Discriminator(chouquetteByte)
  }

  object Painauchocolat {
    val painauchocByte = BreakfastModel(0xC3.toByte)
    implicit val discriminator: Discriminator[Pastry, Painauchocolat, BreakfastModel] = Discriminator(painauchocByte)
  }

  object Painauraisins {
    val painauraisinsByte = BreakfastModel(0xD2.toByte)
    implicit val discriminator: Discriminator[Pastry, Painauraisins, BreakfastModel] = Discriminator(painauraisinsByte)
  }

  object Waffle {
    val waffleByte = BreakfastModel(0xE1.toByte)
    implicit val discriminator: Discriminator[Pastry, Waffle, BreakfastModel] = Discriminator(waffleByte)
  }

  object Pancake {
    val pancakeByte = BreakfastModel(0xF0.toByte)
    implicit val discriminator: Discriminator[Pastry, Pancake, BreakfastModel] = Discriminator(pancakeByte)
  }
}