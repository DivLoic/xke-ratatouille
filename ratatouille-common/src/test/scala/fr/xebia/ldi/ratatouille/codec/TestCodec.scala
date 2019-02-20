package fr.xebia.ldi.ratatouille.codec

import cats.Applicative
import fr.xebia.ldi.ratatouille.codec.Drink.{Beer, OrangeJuice}
import fr.xebia.ldi.ratatouille.codec.Fruit.{Kiwi, Pineapple, Tomato}
import fr.xebia.ldi.ratatouille.codec.Lang.{EN, FR}
import fr.xebia.ldi.ratatouille.codec.Pastry.Croissant
import scodec.bits.BitVector
import scodec.{Attempt, Codec, DecodeResult, SizeBound}
import cats.implicits._

/**
  * Created by loicmdivad.
  */
object TestCodec extends App {

  import scodec.bits._
  import scodec.codecs._
  import scodec.codecs.implicits._

  implicit val attemptApplicative: Applicative[Attempt] = Worksheet.AttemptA()
  implicit val bf2Codec: Codec[Either[Meat, Vector[Pastry]]] = Worksheet.CodecB()

  println(Codec.encode(Breakfast(EN(), Beer(), Pineapple(), Left(Meat(2, 3, 1)))).require.toHex)
  println(Codec.encode(Breakfast(EN(), Beer(), Tomato(), Left(Meat(2, 3, 1)))).require.toByteArray.mkString(", "))

  println(Codec.encode(Breakfast(FR(), OrangeJuice(), Kiwi(), Right(Vector(Croissant())))).require.toHex)
  println(Codec.encode(Breakfast(FR(), OrangeJuice(), Kiwi(), Right(Vector(Croissant())))).require.toByteArray.mkString(", "))

  println(Codec.decode[Breakfast](hex"3300000001a5d1fc".bits))

  val frame1: Array[Byte] = Array(0x33, 0xd4, 0xfc, 0x00, 0x00, 0x00, 0x01, 0xa5).map(_.toByte)

  val frame2: Array[Byte] = Array(0x44, 0xd2, 0xfe, 0x10, 0x02, 0x03, 0x01).map(_.toByte)

  println(Codec.decode[Breakfast](BitVector(frame1)))
  println(Codec.decode[Breakfast](BitVector(frame2)))

}
