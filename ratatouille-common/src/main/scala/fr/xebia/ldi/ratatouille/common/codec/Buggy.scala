package fr.xebia.ldi.ratatouille.common.codec

import fr.xebia.ldi.ratatouille.common.model.Breakfast
import fr.xebia.ldi.ratatouille.common.model.Breakfast.{Fruit, Lang, Liquid, Pastry}
import scodec.bits.BitVector
import scodec.{Attempt, Codec, DecodeResult, Err, SizeBound}
import scodec.codecs._
import scodec.codecs.implicits._

/**
  * Created by loicmdivad.
  */
private[common] object Buggy {

  trait BuggyCodec[T] extends Codec[T] {

    def encode(value: Breakfast): Attempt[BitVector] =
      Attempt.failure(Err("encoding use inside of the deserializer"))

    def sizeBound: SizeBound = scodec.codecs.byte.sizeBound
  }

   lazy val breakfastEvidence: BuggyCodec[Breakfast] = new BuggyCodec[Breakfast] {

    private case class CodecBreakfast(lang: Lang,
                                            liquid: Liquid,
                                            fruit: Fruit,
                                            dishes: Vector[Pastry] = Vector.empty)

    override def decode(bits: BitVector): Attempt[DecodeResult[Breakfast]] =
      Codec.decode[CodecBreakfast](bits).map { result =>
        result.map { cb =>
          Breakfast(cb.lang, cb.liquid, cb.fruit, Right(cb.dishes))
        }
      }
  }
}
