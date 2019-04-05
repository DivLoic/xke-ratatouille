package fr.xebia.ldi.ratatouille.codec

import fr.xebia.ldi.ratatouille.codec.Lunch._
import scodec.codecs.{Discriminated, Discriminator, cstring}

/**
  * Created by loicmdivad.
  */
case class Lunch(name: String, price: Double, `type`: LunchType) extends FoodOrder {

  override def toString: String =
    s"Lunch: $name (${`type`.getClass.getSimpleName.toLowerCase}), price: $price"
}

object Lunch {

  sealed abstract class LunchType
  case class MainDish() extends LunchType
  case class Starter() extends LunchType
  case class Dessert() extends LunchType
  case class Error() extends LunchType

  object MainDish extends MainDish
  object Starter extends Starter
  object Dessert extends Dessert
  object Error extends Error

  implicit val discriminated: Discriminated[LunchType, String] = Discriminated(cstring)

  object LunchType {
      implicit val discriminator1: Discriminator[LunchType, MainDish, String] = Discriminator("main")
      implicit val discriminator2: Discriminator[LunchType, Starter, String] = Discriminator("starter")
      implicit val discriminator3: Discriminator[LunchType, Dessert, String] = Discriminator("dessert")
      implicit val discriminator4: Discriminator[LunchType, Error, String] = Discriminator("error")
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

  class LunchError(line: String) extends
    Lunch(name = s"Error in line: $line", price = Double.MinValue, `type`= Error)
}