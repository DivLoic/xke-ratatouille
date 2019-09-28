package fr.xebia.ldi.ratatouille.common.model

import com.sksamuel.avro4s.{AvroDoc, AvroNamespace, RecordFormat}
import fr.xebia.ldi.ratatouille.common.model.Lunch._
import org.apache.avro.generic.GenericRecord
import scodec.codecs.{Discriminated, Discriminator, cstring}

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
@AvroDoc("Food order corresponding to a lunc")
case class Lunch(@AvroDoc("Full name with side dish") name: String,
                 @AvroDoc("Price in Euro €") price: Double,
                 @AvroDoc("Starter, Main or Dessert") `type`: LunchType) extends FoodOrder {

  override def toAvro: GenericRecord = RecordFormat[Lunch].to(this)

  override def toString: String =
    s"$name (${`type`.toString.toLowerCase}),".padTo(90, " ").mkString("") + s"price: ${price}€"
}

object Lunch {

  @AvroNamespace("ratatouille")
  sealed abstract class LunchType

  case object MainDish extends LunchType
  case object Starter extends LunchType
  case object Dessert extends LunchType
  case object LunchError extends LunchType

  implicit val discriminated: Discriminated[LunchType, String] = Discriminated(cstring)

  object LunchType {
      implicit val discriminator1: Discriminator[LunchType, MainDish.type, String] = Discriminator("main")
      implicit val discriminator2: Discriminator[LunchType, Starter.type, String] = Discriminator("starter")
      implicit val discriminator3: Discriminator[LunchType, Dessert.type, String] = Discriminator("dessert")
      implicit val discriminator4: Discriminator[LunchType, LunchError.type, String] = Discriminator("error")
  }

  val menu: Vector[Lunch] = Vector(
    Lunch("Ratatouille", 10, Starter),
    Lunch("Potato Salardaise", 10, Starter),
    Lunch("Cornet Of Fries & Aioli", 10, Starter),
    Lunch("Oyster Mushrooms & Garlic", 10, Starter),
    Lunch("Potato Salardaise with butter Lettuce", 10, Starter),

    Lunch("Chopped raw steak with french fries", 19, MainDish),
    Lunch("Confit of mulard duck leg with french green lentils", 19, MainDish),
    Lunch("Pan roasted pork chop, parsnips puree", 19, MainDish),
    Lunch("Confit tombo tuna, assorted vegetables, butter lettuce", 19, MainDish), // Salade Nicoise
    Lunch("Pan roasted hanger steak - brussels sprouts (+4€)", 19, MainDish), // Bavette

    Lunch("Cheese: Comté 2016", 9, Dessert),
    Lunch("Fresh seasonal fruit tart", 9, Dessert),
    Lunch("Baba au rhum", 9, Dessert),
    Lunch("Financier: honey yogurt & berry couli", 9, Dessert),
    Lunch("Hazelnut ice cream, warm chocolate sauce", 9, Dessert)
  )

  final class LunchError(line: String) extends
    Lunch(name = s"Error in line: $line", price = Double.MinValue, `type`= LunchError)
}
