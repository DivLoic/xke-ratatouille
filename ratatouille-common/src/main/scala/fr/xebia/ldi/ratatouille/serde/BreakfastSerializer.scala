package fr.xebia.ldi.ratatouille.serde

import java.util

import fr.xebia.ldi.ratatouille.model.Breakfast
import fr.xebia.ldi.ratatouille.model.Breakfast.{Meat, Pastry}
import org.apache.kafka.common.serialization.Serializer
import scodec.{Attempt, Codec, DecodeResult}
import scodec.bits.BitVector
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
class BreakfastSerializer extends Serializer[Breakfast] {

  import BreakfastSerializer.breakfastEvidence

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Breakfast): Array[Byte] =
    Codec.encode(data).map { bytes => bytes.toByteArray }.require

  override def close(): Unit = {}
}

object BreakfastSerializer {

  implicit private[serde] lazy val breakfastEvidence: Codec[Either[Meat, Vector[Pastry]]] =

    new Codec[Either[Meat, Vector[Pastry]]] {

      override def decode(bits: BitVector): Attempt[DecodeResult[Either[Meat, Vector[Pastry]]]] = (for {

        result <- Codec.decode[Vector[Pastry]](bits)

        pastries <- Attempt.successful(result.map(Right[Meat, Vector[Pastry]]))

      } yield pastries)

        .recoverWith[DecodeResult[Either[Meat, Vector[Pastry]]]] {
        case _ => for {

          result <- Meat.codec.decode(bits)

          meat <- Attempt.successful(result.map(Left[Meat, Vector[Pastry]]))

        } yield meat
      }

      override def encode(value: Either[Meat, Vector[Pastry]]): Attempt[BitVector] = value match {
        case Left(meat) => Meat.codec.encode(meat)
        case Right(pastries) => Codec.encode(pastries)
      }

      override def sizeBound = byte.sizeBound
    }
}