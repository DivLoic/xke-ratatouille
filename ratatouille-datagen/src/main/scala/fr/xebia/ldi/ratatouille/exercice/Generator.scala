package fr.xebia.ldi.ratatouille.exercice

import fr.xebia.ldi.ratatouille.model.Drink.{Beer, Coffe, Milk, OrangeJuice, Tea}
import fr.xebia.ldi.ratatouille.model.Fruit.{Banana, Kiwi, Pineapple, Tomato, Watermelon}
import fr.xebia.ldi.ratatouille.model.{Breakfast, Lang, Meat}
import fr.xebia.ldi.ratatouille.model.Lang.{EN, FR}
import fr.xebia.ldi.ratatouille.model.Pastry.{Croissant, Painauchocolat, Painauraisins}
import org.scalacheck.Gen

/**
  * Created by loicmdivad.
  */
object Generator {

  object GeneratorOne {

    def generate(lang: Lang) = lang match {

      case FR() => for {
        drink <- Gen.frequency((1, Milk()), (1, Coffe()), (1, OrangeJuice()))

        fruit <- Gen.frequency((5, Kiwi()), (3, Banana()), (2, Watermelon()))

        pastriesNum <- Gen.chooseNum(0, 8)

        pastries <- Gen.listOfN(pastriesNum, Gen.frequency((5, Croissant()), (4, Painauchocolat()), (1, Painauraisins())))

      } yield Breakfast(lang, drink, fruit, Right(pastries.toVector))

      case EN() => for {
        drink <- Gen.frequency((2, Tea()), (8, Beer()))

        fruit <- Gen.frequency((5, Banana()), (3, Pineapple()))

        List(sausages, beacons, eggs) <- Gen.listOfN(3, Gen.chooseNum(0, 4))

      } yield Breakfast(lang, drink, fruit, Left(Meat(sausages, beacons, eggs)))
    }
  }

}
