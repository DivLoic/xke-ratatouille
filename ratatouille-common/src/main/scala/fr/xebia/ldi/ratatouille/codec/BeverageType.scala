package fr.xebia.ldi.ratatouille.codec

import io.circe.Encoder

/**
  * Created by loicmdivad.
  */
sealed trait BeverageType

object BeverageType {

  implicit val encoder: Encoder[BeverageType] = Encoder.forProduct1("type"){
    t => t.getClass.getSimpleName.stripSuffix("$")
  }

  case object Wine extends BeverageType
  case object Rhum extends BeverageType
  case object Water extends BeverageType
  case object Whisky extends BeverageType
  case object Champagne extends BeverageType

}
