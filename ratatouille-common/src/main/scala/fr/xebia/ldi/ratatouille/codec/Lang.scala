package fr.xebia.ldi.ratatouille.codec

import scodec.codecs._

/**
  * Created by loicmdivad.
  */
sealed trait Lang

object Lang {

  case class FR() extends Lang

  case class EN() extends Lang

  implicit val discriminated: Discriminated[Lang, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

  object FR {
    private val frByte = BreakfastModel(0x33.toByte)
    implicit val discriminator: Discriminator[Lang, FR, BreakfastModel] = Discriminator(frByte)
  }

  object EN {
    private val enByte = BreakfastModel(0x44.toByte)
    implicit val discriminator: Discriminator[Lang, EN, BreakfastModel] = Discriminator(enByte)
  }
}
