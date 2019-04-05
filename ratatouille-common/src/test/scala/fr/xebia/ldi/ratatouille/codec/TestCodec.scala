package fr.xebia.ldi.ratatouille.codec

import java.time.{Instant, ZoneId}

import cats.Applicative
import scodec.bits.BitVector
import scodec.{Attempt, Codec, DecodeResult, SizeBound}
import cats.implicits._
import fr.xebia.ldi.ratatouille.codec.Drink.Rhum
import fr.xebia.ldi.ratatouille.codec.Breakfast.Boose.{Beer, Milk, OrangeJuice}
import fr.xebia.ldi.ratatouille.codec.Breakfast.Fruit.{Kiwi, Pineapple, Tomato}
import fr.xebia.ldi.ratatouille.codec.Breakfast.Lang.{EN, FR}
import fr.xebia.ldi.ratatouille.codec.Breakfast.Pastry.Croissant
import fr.xebia.ldi.ratatouille.codec.Breakfast.{Lang, Meat, Pastry}
import fr.xebia.ldi.ratatouille.codec.Dinner.{Client, Command, Moment}


/**
  * Created by loicmdivad.
  */
object TestCodec extends App {

  import scodec.bits._
  import scodec.codecs._
  import scodec.codecs.implicits._

  implicit val attemptApplicative: Applicative[Attempt] = Worksheet.AttemptA()
  implicit val bf2Codec: Codec[Either[Meat, Vector[Pastry]]] = Worksheet.CodecB()

  // Breakfast__________________________________________________________________________________________________________
  println("Breakfast")
  println(Codec.encode[FoodOrder](Breakfast(EN(), Milk(), Pineapple(), Left(Meat(2, 3, 1)))).require)
  println(Codec.decode[FoodOrder](hex"0044d1fe10020301".bits).require)

  (0 until 3) foreach (_ => println(""))

  // Lunch______________________________________________________________________________________________________________
  println("Lunch")
  println(Codec.encode[FoodOrder](Lunch("Some awesome food name", 2.0, Lunch.MainDish())).require)
  println(Codec.decode[FoodOrder](hex"0100000016536f6d6520617765736f6d6520666f6f64206e616d6540000000000000006d61696e00".bits).require)

  (0 until 3) foreach (_ => println(""))

  // Beverage______________________________________________________________________________________________________________
  println("Beverage")
  println(Codec.encode[FoodOrder](Drink("Drink Name", Rhum, 1, None)).require)
  println(Codec.decode[FoodOrder](hex"020000000a4472696e6b204e616d655268756d000000000100".bits).require)

  (0 until 3) foreach (_ => println(""))

/*  // Drink______________________________________________________________________________________________________________
  println("Drink")
  println(Codec.encode[FoodOrder](Drink("Drink Name", 2.0)).require)
  println(Codec.decode[FoodOrder](hex"020000000a4472696e6b204e616d654000000000000000".bits).require)

  (0 until 3) foreach (_ => println(""))*/

  // Dinner_____________________________________________________________________________________________________________
  println("Dinner")
  println(Codec.encode[FoodOrder](Dinner(Command("Drink Name", 2.0), None, Moment(0L, "Europe/Paris"))).require)
  println(Codec.decode[FoodOrder](hex"030000000a4472696e6b204e616d6540000000000000000000000000000000000000000c4575726f70652f5061726973".bits).require)

  (0 until 3) foreach (_ => println(""))







  val frame1: Array[Byte] = Array(0x33, 0xd4, 0xfc, 0x00, 0x00, 0x00, 0x01, 0xa5).map(_.toByte)

  val frame2: Array[Byte] = Array(0x44, 0xd2, 0xfe, 0x10, 0x02, 0x03, 0x01).map(_.toByte)
}
