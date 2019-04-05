package fr.xebia.ldi.ratatouille.codec

import fr.xebia.ldi.ratatouille.codec.Breakfast._
import scodec.bits.BitVector
import scodec.{Attempt, Codec, DecodeResult, SizeBound, codecs}
import scodec.codecs.{Discriminated, Discriminator, byte, uint8}

/**
  * Created by loicmdivad.
  */
case class Breakfast(lang: Lang,
                     drink: Boose,
                     fruit: Fruit,
                     dishes: Either[Meat, Vector[Pastry]] = Right(Vector.empty)) extends FoodOrder

object Breakfast {

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

  sealed trait Lang

  object Lang {

    case class FR() extends Lang

    case class EN() extends Lang

    implicit val discriminated: Discriminated[Lang, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

    object FR {
      private val frByte = BreakfastModel(0x33.toByte)
      implicit val discriminator: Discriminator[Lang, FR, BreakfastModel] = Discriminator(frByte)
    }

    object EN {
      private val enByte = BreakfastModel(0x44.toByte)
      implicit val discriminator: Discriminator[Lang, EN, BreakfastModel] = Discriminator(enByte)
    }
  }

  sealed trait Boose

  object Boose {

    case class Tea() extends Boose

    case class Milk() extends Boose

    case class Beer() extends Boose

    case class Coffe() extends Boose

    case class AppleJuice() extends Boose

    case class OrangeJuice() extends Boose

    implicit val discriminated: Discriminated[Boose, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

    object Tea {
      val teaByte = BreakfastModel(0xD0.toByte)
      implicit val discriminator: Discriminator[Boose, Tea, BreakfastModel] = Discriminator(teaByte)
    }

    object Milk {
      val milkByte = BreakfastModel(0xD1.toByte)
      implicit val discriminator: Discriminator[Boose, Milk, BreakfastModel] = Discriminator(milkByte)
    }

    object Beer {
      val beerByte = BreakfastModel(0xD2.toByte)
      implicit val discriminator: Discriminator[Boose, Beer, BreakfastModel] = Discriminator(beerByte)
    }

    object Coffe {
      val coffeByte = BreakfastModel(0xD3.toByte)
      implicit val discriminator: Discriminator[Boose, Coffe, BreakfastModel] = Discriminator(coffeByte)
    }

    object OrangeJuice {
      val orangeByte = BreakfastModel(0xD4.toByte)
      implicit val discriminator: Discriminator[Boose, OrangeJuice, BreakfastModel] = Discriminator(orangeByte)
    }

    object AppleJuice {
      val appleByte = BreakfastModel(0xD5.toByte)
      implicit val discriminator: Discriminator[Boose, AppleJuice, BreakfastModel] = Discriminator(appleByte)
    }
  }

  sealed trait Fruit

  object Fruit {

    case class Kiwi() extends Fruit
    case class Banana() extends Fruit
    case class Tomato() extends Fruit
    case class Pineapple() extends Fruit
    case class Watermelon() extends Fruit

    implicit val discriminated: Discriminated[Fruit, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

    object Kiwi {
      private val kiwiByte = BreakfastModel(0xFB.toByte)
      implicit val discriminator: Discriminator[Fruit, Kiwi, BreakfastModel] = Discriminator(kiwiByte)
    }

    object Banana {
      private val bananaByte = BreakfastModel(0xFC.toByte)
      implicit val discriminator: Discriminator[Fruit, Banana, BreakfastModel] = Discriminator(bananaByte)
    }

    object Tomato {
      private val tomatoByte = BreakfastModel(0xFD.toByte)
      implicit val discriminator: Discriminator[Fruit, Tomato, BreakfastModel] = Discriminator(tomatoByte)
    }

    object Pineapple {
      private val pineAppleByte = BreakfastModel(0xFE.toByte)
      implicit val discriminator: Discriminator[Fruit, Pineapple, BreakfastModel] = Discriminator(pineAppleByte)
    }

    object Watermelon {
      private val watermelonByte = BreakfastModel(0xFF.toByte)
      implicit val discriminator: Discriminator[Fruit, Watermelon, BreakfastModel] = Discriminator(watermelonByte)
    }
  }

  case class Meat(sausages: Int, beacons: Int, eggs: Int)

  object Meat {

    implicit lazy val codec: Codec[Meat] = (codecs.constant(16) :: uint8 :: uint8 :: uint8).as[Meat]
  }

  sealed trait Pastry

  object Pastry {

    case class Croissant() extends Pastry

    case class Chouquette() extends Pastry

    case class Painauchocolat() extends Pastry

    case class Painauraisins() extends Pastry

    case class Waffle() extends Pastry

    case class Pancake() extends Pastry

    implicit val discriminated: Discriminated[Pastry, BreakfastModel] = Discriminated(BreakfastModel.breakfastCodec)

    object Croissant {
      val croissantByte = BreakfastModel(0xA5.toByte)
      implicit val discriminator: Discriminator[Pastry, Croissant, BreakfastModel] = Discriminator(croissantByte)
    }

    object Chouquette {
      val chouquetteByte = BreakfastModel(0xB4.toByte)
      implicit val discriminator: Discriminator[Pastry, Chouquette, BreakfastModel] = Discriminator(chouquetteByte)
    }

    object Painauchocolat {
      val painauchocByte = BreakfastModel(0xC3.toByte)
      implicit val discriminator: Discriminator[Pastry, Painauchocolat, BreakfastModel] = Discriminator(painauchocByte)
    }

    object Painauraisins {
      val painauraisinsByte = BreakfastModel(0xD2.toByte)
      implicit val discriminator: Discriminator[Pastry, Painauraisins, BreakfastModel] = Discriminator(painauraisinsByte)
    }

    object Waffle {
      val waffleByte = BreakfastModel(0xE1.toByte)
      implicit val discriminator: Discriminator[Pastry, Waffle, BreakfastModel] = Discriminator(waffleByte)
    }

    object Pancake {
      val pancakeByte = BreakfastModel(0xF0.toByte)
      implicit val discriminator: Discriminator[Pastry, Pancake, BreakfastModel] = Discriminator(pancakeByte)
    }

  }
}