package fr.xebia.ldi.ratatouille

import cats.Applicative
import fr.xebia.ldi.ratatouille.model.Breakfast.{Meat, Pastry}
import scodec.bits.BitVector
import scodec.codecs.{byte, cstring}
import scodec.{Attempt, Codec, DecodeResult, SizeBound}

/**
  * Created by loicmdivad.
  */
package object codec {

  implicit lazy val symbolCodec: Codec[Symbol] = new Codec[Symbol] {

    override def decode(bits: BitVector): Attempt[DecodeResult[Symbol]] =
      cstring.decode(bits).map(_.map(Symbol(_)))

    override def encode(value: Symbol): Attempt[BitVector] =
      cstring.encode(value.name)

    override def sizeBound: SizeBound = byte.sizeBound
  }

  implicit private lazy val applicativeAttempt: Applicative[Attempt] = new Applicative[Attempt]{
    override def pure[A](x: A): Attempt[A] = Attempt.successful(x)
    override def ap[A, B](ff: Attempt[A => B])(fa: Attempt[A]): Attempt[B] = for { a <- fa; f <- ff } yield f(a)
  }

  implicit lazy val breakfastDishCodec: Codec[Either[Meat, Vector[Pastry]]] =

    new Codec[Either[Meat, Vector[Pastry]]] {

      import cats.syntax.applicative._
      import scodec.codecs.implicits._

      override def decode(bits: BitVector): Attempt[DecodeResult[Either[Meat, Vector[Pastry]]]] = (for {

        result <- Codec.decode[Vector[Pastry]](bits)

        pastries <- result.map(Right[Meat, Vector[Pastry]]).pure[Attempt]

      } yield pastries)

        .recoverWith[DecodeResult[Either[Meat, Vector[Pastry]]]] {

        case _ => for {

          result <- Meat.codec.decode(bits)

          meat <- result.map(Left[Meat, Vector[Pastry]]).pure[Attempt]

        } yield meat
      }

      override def encode(value: Either[Meat, Vector[Pastry]]): Attempt[BitVector] = value match {
        case Left(meat) => Meat.codec.encode(meat)
        case Right(pastries) => Codec.encode(pastries)
      }

      override def sizeBound = byte.sizeBound
    }

}
