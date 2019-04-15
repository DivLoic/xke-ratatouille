package fr.xebia.ldi.ratatouille.common.model

import scodec.Codec
import scodec.bits.BitVector
import fr.xebia.ldi.ratatouille.common.model.Breakfast._
import fr.xebia.ldi.ratatouille.common.model.BreakfastDraft.frame2

/**
  * Created by loicmdivad.
  */
object BreakfastDraft extends App {

  import scodec.codecs.implicits._
  import fr.xebia.ldi.ratatouille.common.codec.breakfastDishCodec


  case class Breakfast1(lang: Lang,
                        drink: Liquid,
                        fruit: Fruit,
                        dishes: Vector[Pastry] = Vector.empty)

  case class Breakfast2(lang: Lang,
                        drink: Liquid,
                        fruit: Fruit,
                        dishes: Either[Meat, Vector[Pastry]])

  val frame1: Array[Byte] = Array(0x33, 0xd4, 0xfc, 0x00, 0x00, 0x00, 0x01, 0xc3).map(_.toByte)

  val frame2: Array[Byte] = Array(0x44, 0xd2, 0xfe, 0x10, 0x02, 0x03, 0x01).map(_.toByte)

  println(s"Codec 1 / Frame A: ${Codec.decode[Breakfast1](BitVector(frame1))}")
  println()
  println(s"Codec 1 / Frame B: ${Codec.decode[Breakfast1](BitVector(frame2))}")

  (0 to 20) foreach (_ => println())

  println(s"Codec 2 / Frame A: ${Codec.decode[Breakfast2](BitVector(frame1))}")
  println()
  println(s"Codec 2 / Frame B: ${Codec.decode[Breakfast2](BitVector(frame2))}")

}
