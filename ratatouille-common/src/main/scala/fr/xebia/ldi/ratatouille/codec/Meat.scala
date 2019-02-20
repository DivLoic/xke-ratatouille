package fr.xebia.ldi.ratatouille.codec

import scodec.codecs.uint8
import scodec.{Codec, codecs}

/**
  * Created by loicmdivad.
  */
case class Meat(sausages: Int, beacons: Int, eggs: Int)

object Meat {

  implicit lazy val codec: Codec[Meat] = (codecs.constant(16) :: uint8 :: uint8 :: uint8).as[Meat]
}
