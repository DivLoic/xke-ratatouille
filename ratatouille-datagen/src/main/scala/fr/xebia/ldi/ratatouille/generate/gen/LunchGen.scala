package fr.xebia.ldi.ratatouille.generate.gen

import fr.xebia.ldi.ratatouille.common.model.Lunch
import fr.xebia.ldi.ratatouille.common.model.Lunch.{Dessert, MainDish, Starter}
import org.scalacheck.Gen.oneOf
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by loicmdivad.
  */
trait LunchGen {

  def generate: Gen[Lunch] = for {

    dishType <- Gen.frequency((1, Starter), (5, MainDish), (4, Dessert))

    dish <- Arbitrary(oneOf(Lunch.menu)).arbitrary.suchThat(_.`type` == dishType)

  } yield dish

}
