package fr.xebia.ldi.ratatouille.generate.gen

import fr.xebia.ldi.ratatouille.common.model.Breakfast
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Fruit.{Banana, Kiwi, Pineapple, Watermelon}
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Lang.{EN, FR}
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Liquid.{Beer, Coffe, Milk, OrangeJuice, Tea}
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Meat
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Pastry.{Croissant, Painauchocolat, Painauraisins}
import org.scalacheck.Gen

/**
  * Created by loicmdivad.
  */
trait BreakfastGen {

  def generateFr: Gen[Breakfast] = for {

    drink <- Gen.frequency((1, Milk()), (1, Coffe()), (1, OrangeJuice()))

    fruit <- Gen.frequency((5, Kiwi()), (3, Banana()), (2, Watermelon()))

    pastriesNum <- Gen.chooseNum(0, 8)

    pastries <- Gen.listOfN(pastriesNum, Gen.frequency((5, Croissant()), (4, Painauchocolat()), (1, Painauraisins())))

  } yield Breakfast(FR, drink, fruit, Right(pastries.toVector))

  def generateEn: Gen[Breakfast] = for {

    drink <- Gen.frequency((2, Tea()), (8, Beer()))

    fruit <- Gen.frequency((5, Banana()), (3, Pineapple()))

    List(sausages, beacons, eggs) <- Gen.listOfN(3, Gen.chooseNum(0, 4))

  } yield Breakfast(EN, drink, fruit, Left(Meat(sausages, beacons, eggs)))
}
