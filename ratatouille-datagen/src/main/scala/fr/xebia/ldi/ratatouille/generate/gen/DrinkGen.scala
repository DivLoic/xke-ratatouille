package fr.xebia.ldi.ratatouille.generate.gen

import fr.xebia.ldi.ratatouille.common.model.Drink
import fr.xebia.ldi.ratatouille.common.model.Drink.{Champagne, DrinkType, Rhum, Water, Whisky, Wine}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.oneOf

/**
  * Created by loicmdivad.
  */
trait DrinkGen {

  def generate[T <: DrinkType](`type`: T): Gen[Drink] = `type` match {
    case Wine => Arbitrary(oneOf(Drink.wines)).arbitrary
    case Rhum => Arbitrary(oneOf(Drink.rhums)).arbitrary
    case Water => Arbitrary(oneOf(Drink.waters)).arbitrary
    case Whisky => Arbitrary(oneOf(Drink.whiskies)).arbitrary
    case Champagne => Arbitrary(oneOf(Drink.champagnes)).arbitrary
  }

}
