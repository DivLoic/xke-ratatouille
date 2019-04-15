package fr.xebia.ldi.ratatouille.common.model

import fr.xebia.ldi.ratatouille.common.model.Breakfast.Fruit.Pineapple
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Lang.EN
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Liquid.Beer
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Meat
import fr.xebia.ldi.ratatouille.common.model.Dinner.{Command, Moment}
import fr.xebia.ldi.ratatouille.common.model.Drink.Rhum
import scodec.Codec
import scodec.bits._

/**
  * Created by loicmdivad.
  */
object FoodOrderDraft extends App {

  import FoodOrder.foodCodec

  // Breakfast__________________________________________________________________________________________________________
  println("Breakfast")
  println(Codec.encode[FoodOrder](Breakfast(EN(), Beer(), Pineapple(), Left(Meat(2, 3, 1)))).require)
  printf(Codec.decode[FoodOrder](hex"0044d1fe10020301".bits).require.toString)

  (0 until 3) foreach (_ => println())

  // Lunch______________________________________________________________________________________________________________
  println("Lunch")
  println(Codec.encode[FoodOrder](Lunch("Some awesome food name", 2.0, Lunch.MainDish())).require)
  println(Codec.decode[FoodOrder](hex"0100000016536f6d6520617765736f6d6520666f6f64206e616d6540000000000000006d61696e00".bits).require)

  (0 until 3) foreach (_ => println())

  // Drink______________________________________________________________________________________________________________
  println("Beverage")
  println(Codec.encode[FoodOrder](Drink("Drink Name", Rhum, 1, None)).require)
  println(Codec.decode[FoodOrder](hex"020000000a4472696e6b204e616d655268756d000000000100".bits).require)

  (0 until 3) foreach (_ => println())

  // Dinner_____________________________________________________________________________________________________________
  println("Dinner")
  println(Codec.encode[FoodOrder](Dinner(Command("Drink Name", 2.0), None, Moment("Europe/Paris", 0L), "ECT")).require)
  println(Codec.decode[FoodOrder](hex"030000000a4472696e6b204e616d654000000000000000000000000c4575726f70652f5061726973000000000000000000000003454354".bits).require)
}
