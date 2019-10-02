package fr.xebia.ldi.ratatouille.common.model

import com.sksamuel.avro4s.{AvroDoc, AvroNamespace, RecordFormat}
import fr.xebia.ldi.ratatouille.common.model.Breakfast._
import org.apache.avro.generic.GenericRecord
import scodec.codecs.{Discriminated, Discriminator, uint8}
import scodec.{Codec, codecs}

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
@AvroDoc("Food order corresponding to a breakfast")
case class Breakfast(@AvroDoc("Language of the food order, depends on the origin of the event") lang: Lang,
                     @AvroDoc("Standard name of the drink, without quantity") liquid: Liquid,
                     @AvroDoc("Standard name of the fruit, without number") fruit: Fruit,
                     @AvroDoc("Tail of the frame, either a collection of pasties or a number of bacon slices")
                     dishes: Either[Meat, Vector[Pastry]]) extends FoodOrder {

  override def toAvro: GenericRecord = RecordFormat[Breakfast].to(this)

  override def toString =
      s"($lang) " +
      s"drink: $liquid,  ".padTo(25, " ").mkString("") +
      s"fruits: $fruit, ".padTo(25, " ").mkString("") +
      s"dishes: " + s"${dishes match {
        case Right(pastries) => pastries.map(_.toString.takeWhile(_ != '('))
        case Left(meat) => meat
      }}"
}

object Breakfast {

  @AvroNamespace("ratatouille")
  sealed trait Lang

  object Lang {
    case object FR extends Lang
    case object EN extends Lang

    implicit val discLang: Discriminated[Lang, Byte] = Discriminated(scodec.codecs.byte)
    implicit val discFr: Discriminator[Lang, FR.type , Byte] = Discriminator(0x33)
    implicit val discEn: Discriminator[Lang, EN.type , Byte] = Discriminator(0x44)
  }

  @AvroNamespace("ratatouille")
  sealed trait Liquid

  object Liquid {

    case object Tea extends Liquid
    case object Milk extends Liquid
    case object Beer extends Liquid
    case object Coffe extends Liquid
    case object AppleJuice extends Liquid
    case object OrangeJuice extends Liquid

    implicit val discBoose: Discriminated[Liquid, Byte] = Discriminated(scodec.codecs.byte)

    implicit val discTea: Discriminator[Liquid, Tea.type, Byte] = Discriminator(0xD0.toByte)
    implicit val discMilk: Discriminator[Liquid, Milk.type, Byte] = Discriminator(0xD1.toByte)
    implicit val discBeer: Discriminator[Liquid, Beer.type, Byte] = Discriminator(0xD2.toByte)
    implicit val discCoffe: Discriminator[Liquid, Coffe.type, Byte] = Discriminator(0xD3.toByte)
    implicit val discOrangeJuice: Discriminator[Liquid, OrangeJuice.type, Byte] = Discriminator(0xD4.toByte)
    implicit val discAppleJuice: Discriminator[Liquid, AppleJuice.type, Byte] = Discriminator(0xD5.toByte)
  }

  @AvroNamespace("ratatouille")
  sealed trait Fruit

  object Fruit {

    case object Kiwi extends Fruit
    case object Banana extends Fruit
    case object Tomato extends Fruit
    case object Pineapple extends Fruit
    case object Watermelon extends Fruit

    implicit val discFruit: Discriminated[Fruit, Byte] = Discriminated(scodec.codecs.byte)

    implicit val discKiwi: Discriminator[Fruit, Kiwi.type, Byte] = Discriminator(0xFB.toByte)
    implicit val discBanana: Discriminator[Fruit, Banana.type, Byte] = Discriminator(0xFC.toByte)
    implicit val discTomato: Discriminator[Fruit, Tomato.type, Byte] = Discriminator(0xFD.toByte)
    implicit val discPineapple: Discriminator[Fruit, Pineapple.type, Byte] = Discriminator(0xFE.toByte)
    implicit val discWatermelon: Discriminator[Fruit, Watermelon.type, Byte] = Discriminator(0xFF.toByte)

  }

  @AvroNamespace("ratatouille")
  case class Meat(sausages: Int, bacons: Int, eggs: Int) {
    override def toString: String = s"Meat(sausages = $sausages, bacons = $bacons, eggs = $eggs)"
  }

  object Meat {

    implicit lazy val codec: Codec[Meat] = (codecs.constant(16) :: uint8 :: uint8 :: uint8).as[Meat]
  }

  @AvroNamespace("ratatouille")
  sealed trait Pastry

  object Pastry {

    case object Croissant extends Pastry
    case object Chouquette extends Pastry
    case object Painauchocolat extends Pastry
    case object Painauraisins extends Pastry
    case object Waffle extends Pastry
    case object Pancake extends Pastry

    implicit val discPastry: Discriminated[Pastry, Byte] = Discriminated(scodec.codecs.byte)

    implicit val discCroissant: Discriminator[Pastry, Croissant.type, Byte] = Discriminator(0xA5.toByte)
    implicit val discChouquette: Discriminator[Pastry, Chouquette.type, Byte] = Discriminator(0xB4.toByte)
    implicit val discPainauchocolat: Discriminator[Pastry, Painauchocolat.type, Byte] = Discriminator(0xC3.toByte)
    implicit val discPainauraisins: Discriminator[Pastry, Painauraisins.type, Byte] = Discriminator(0xD2.toByte)
    implicit val discWaffle: Discriminator[Pastry, Waffle.type, Byte] = Discriminator(0xE1.toByte)
    implicit val discPancake: Discriminator[Pastry, Pancake.type, Byte] = Discriminator(0xF0.toByte)
  }
}