package fr.xebia.ldi.ratatouille.codec

import scodec.bits.BitVector
import scodec.codecs._
import scodec.{Attempt, Codec, DecodeResult, SizeBound}


/**
  * Created by loicmdivad.
  */
case class BreakfastModel(byte: Byte) extends AnyVal

object BreakfastModel {

  implicit val breakfastCodec: Codec[BreakfastModel] = new Codec[BreakfastModel] {

    override def encode(value: BreakfastModel): Attempt[BitVector] = byte.encode(value.byte)

    override def sizeBound: SizeBound = byte.sizeBound

    override def decode(bits: BitVector): Attempt[DecodeResult[BreakfastModel]] = byte.decode(bits).map { result =>
      result.map(BreakfastModel(_))
    }
  }
}