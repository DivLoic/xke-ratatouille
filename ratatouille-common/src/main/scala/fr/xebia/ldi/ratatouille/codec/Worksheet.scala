package fr.xebia.ldi.ratatouille.codec

import cats.Applicative
import scodec.{Attempt, Codec, DecodeResult}
import scodec.bits.BitVector
import scodec.codecs.byte
import cats.implicits._
import fr.xebia.ldi.ratatouille.codec.Breakfast.{Meat, Pastry}
import scodec.bits._
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
object Worksheet {

  def AttemptA():  Applicative[Attempt] = new Applicative[Attempt]{
    override def pure[A](x: A): Attempt[A] = Attempt.successful(x)
    override def ap[A, B](ff: Attempt[A => B])(fa: Attempt[A]): Attempt[B] = for { a <- fa; f <- ff } yield f(a)
  }

  def CodecB(): Codec[Either[Meat, Vector[Pastry]]] = new Codec[Either[Meat, Vector[Pastry]]] {

    override def decode(bits: BitVector): Attempt[DecodeResult[Either[Meat, Vector[Pastry]]]] = (for {

      result <- Codec.decode[Vector[Pastry]](bits)

      pastries <- result.map(Right[Meat, Vector[Pastry]]).pure[Attempt](AttemptA())

    } yield pastries)

      .recoverWith[DecodeResult[Either[Meat, Vector[Pastry]]]] {

      case _ => for {

        result <- Meat.codec.decode(bits)

        meat <- result.map(Left[Meat, Vector[Pastry]]).pure[Attempt](AttemptA())

      } yield meat
    }

    override def encode(value: Either[Meat, Vector[Pastry]]): Attempt[BitVector] = value match {
      case Left(meat) => Meat.codec.encode(meat)
      case Right(pastries) => Codec.encode(pastries)
    }

    override def sizeBound = byte.sizeBound
  }

}
