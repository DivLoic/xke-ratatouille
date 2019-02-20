package fr.xebia.ldi.ratatouille.codec

/**
  * Created by loicmdivad.
  */
case class Breakfast(lang: Lang,
                     drink: Drink,
                     fruit: Fruit,
                     dishes: Either[Meat, Vector[Pastry]] = Right(Vector.empty))