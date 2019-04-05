package fr.xebia.ldi.ratatouille.codec

import scodec.Codec
import scodec.codecs.CoproductCodecBuilder
import shapeless.{:+:, CNil, HNil}

/**
  * Created by loicmdivad.
  */
object Wip extends App {

  trait Foo
  case class Foo1() extends Foo
  case class Foo2() extends Foo
  case class Foo3() extends Foo
  case class Foo4() extends Foo

  val bar1: Codec[Foo1] = ???
  val bar2: Codec[Foo2] = ???
  val bar3: Codec[Foo3] = ???
  val bar4: Codec[Foo4] = ???

}
