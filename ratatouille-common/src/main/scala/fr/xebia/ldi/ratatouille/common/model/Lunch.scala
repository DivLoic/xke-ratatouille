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
    Lunch("Pâté en croûte de canard et foie gras", 10, Starter),
    Lunch("Carpaccio de boeuf fumé - sauce ravigote", 10, Starter),
    Lunch("Poireaux crayons - aile de raie - vinaigrette à la truffe", 10, Starter),
    Lunch("Raviolis de canard - bouillon pot-au-feu. (+2€)", 10, Starter),
    Lunch("Crème de pois cassés - petits croûtons - chorizo", 10, Starter),

    Lunch("Filet de maigre - épinard au beurre - émulsion de crustacés", 19, MainDish),
    Lunch("Brandade de haddock au chou vert", 19, MainDish),
    Lunch("Travers de cochon braisé - lentilles", 19, MainDish),
    Lunch("Araignée de cochon à la plancha - panais - chou braisé", 19, MainDish),
    Lunch("Bavette de veau au saté - pommes grenailles - choux de Bruxelles (+4€)", 19, MainDish),

    Lunch("Fromage: Comté 2016", 9, Dessert),
    Lunch("Oeuf à la neige et pralines roses", 9, Dessert),
    Lunch("Baba au rhum ambré - crème légère au miel d'acacia", 9, Dessert),
    Lunch("Crumble - ganache chocolat noir", 9, Dessert),
    Lunch("Riz au lait à la vanille - caramel au beurre salé", 9, Dessert)
  )

  final class LunchError(line: String) extends
    Lunch(name = s"Error in line: $line", price = Double.MinValue, `type`= LunchError)
}
