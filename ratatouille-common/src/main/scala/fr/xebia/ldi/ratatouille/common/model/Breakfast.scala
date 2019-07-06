package fr.xebia.ldi.ratatouille.common.model

import com.sksamuel.avro4s.{AvroNamespace, Record, RecordFormat}
import fr.xebia.ldi.ratatouille.common.model.Breakfast.Lang.{EN, FR}
import fr.xebia.ldi.ratatouille.common.model.Breakfast._
import org.apache.avro.generic.GenericRecord
import scodec.codecs.{Discriminated, Discriminator, uint8}
import scodec.{Codec, codecs}

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
case class Breakfast(lang: Lang,
                     liquid: Liquid,
                     fruit: Fruit,
                     dishes: Either[Meat, Vector[Pastry]] = Right(Vector.empty)) extends FoodOrder {

  override def toAvro: GenericRecord = RecordFormat[Breakfast].to(this)

  override def toString =
      s"(${lang match {case FR() => "Fr"; case EN() => "En"}}) " +
      s"drink: $liquid,  ".padTo(25, " ").mkString("") +
      s"fruits: $fruit, ".padTo(25, " ").mkString("") +
      s"dishes: " + s"${dishes match {
        case Right(pastries) => pastries.map(_.toString.takeWhile(_ != '('))
        case Left(meat) => meat
      }}"
}

object Breakfast {

  sealed trait Lang

  object Lang {

    case class FR() extends Lang
    case class EN() extends Lang

    object FR extends FR
    object EN extends EN

    implicit val discLang: Discriminated[Lang, Byte] = Discriminated(scodec.codecs.byte)
    implicit val discFr: Discriminator[Lang, FR, Byte] = Discriminator(0x33)
    implicit val discEn: Discriminator[Lang, EN, Byte] = Discriminator(0x44)

  }

  sealed trait Liquid

  object Liquid {

    case class Tea() extends Liquid
    case class Milk() extends Liquid
    case class Beer() extends Liquid
    case class Coffe() extends Liquid
    case class AppleJuice() extends Liquid
    case class OrangeJuice() extends Liquid

    implicit val discBoose: Discriminated[Liquid, Byte] = Discriminated(scodec.codecs.byte)

    implicit val discTea: Discriminator[Liquid, Tea, Byte] = Discriminator(0xD0.toByte)
    implicit val discMilk: Discriminator[Liquid, Milk, Byte] = Discriminator(0xD1.toByte)
    implicit val discBeer: Discriminator[Liquid, Beer, Byte] = Discriminator(0xD2.toByte)
    implicit val discCoffe: Discriminator[Liquid, Coffe, Byte] = Discriminator(0xD3.toByte)
    implicit val discOrangeJuice: Discriminator[Liquid, OrangeJuice, Byte] = Discriminator(0xD4.toByte)
    implicit val discAppleJuice: Discriminator[Liquid, AppleJuice, Byte] = Discriminator(0xD5.toByte)
  }

  sealed trait Fruit

  object Fruit {

    case class Kiwi() extends Fruit
    case class Banana() extends Fruit
    case class Tomato() extends Fruit
    case class Pineapple() extends Fruit
    case class Watermelon() extends Fruit

    implicit val discFruit: Discriminated[Fruit, Byte] = Discriminated(scodec.codecs.byte)

    implicit val discKiwi: Discriminator[Fruit, Kiwi, Byte] = Discriminator(0xFB.toByte)
    implicit val discBanana: Discriminator[Fruit, Banana, Byte] = Discriminator(0xFC.toByte)
    implicit val discTomato: Discriminator[Fruit, Tomato, Byte] = Discriminator(0xFD.toByte)
    implicit val discPineapple: Discriminator[Fruit, Pineapple, Byte] = Discriminator(0xFE.toByte)
    implicit val discWatermelon: Discriminator[Fruit, Watermelon, Byte] = Discriminator(0xFF.toByte)

  }

  case class Meat(sausages: Int, beacons: Int, eggs: Int) {
    override def toString: String = s"Meat(sausages = $sausages, beacons = $beacons, eggs = $eggs)"
  }

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

    implicit val discPastry: Discriminated[Pastry, Byte] = Discriminated(scodec.codecs.byte)

    implicit val discCroissant: Discriminator[Pastry, Croissant, Byte] = Discriminator(0xA5.toByte)
    implicit val discChouquette: Discriminator[Pastry, Chouquette, Byte] = Discriminator(0xB4.toByte)
    implicit val discPainauchocolat: Discriminator[Pastry, Painauchocolat, Byte] = Discriminator(0xC3.toByte)
    implicit val discPainauraisins: Discriminator[Pastry, Painauraisins, Byte] = Discriminator(0xD2.toByte)
    implicit val discWaffle: Discriminator[Pastry, Waffle, Byte] = Discriminator(0xE1.toByte)
    implicit val discPancake: Discriminator[Pastry, Pancake, Byte] = Discriminator(0xF0.toByte)
  }
}