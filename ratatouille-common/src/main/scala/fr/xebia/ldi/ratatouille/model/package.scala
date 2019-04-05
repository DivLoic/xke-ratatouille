package fr.xebia.ldi.ratatouille

import scodec.bits.BitVector
import scodec.codecs.{byte, cstring}
import scodec.{Attempt, Codec, DecodeResult, SizeBound}

/**
  * Created by loicmdivad.
  */
package object model {

  implicit lazy val symbolCodec: Codec[Symbol] = new Codec[Symbol] {

    override def decode(bits: BitVector): Attempt[DecodeResult[Symbol]] =
      cstring.decode(bits).map(_.map(Symbol(_)))

    override def encode(value: Symbol): Attempt[BitVector] =
      cstring.encode(value.name)

    override def sizeBound: SizeBound = byte.sizeBound
  }

}
