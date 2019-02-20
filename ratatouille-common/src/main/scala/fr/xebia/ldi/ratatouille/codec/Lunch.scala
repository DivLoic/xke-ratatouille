package fr.xebia.ldi.ratatouille.codec

import fr.xebia.ldi.ratatouille.codec.Lunch._
import purecsv.safe.converter.StringConverter

import scala.util.Try

/**
  * Created by loicmdivad.
  */
case class Lunch(name: String, price: Double, `type`: Dish) {

  override def toString: String = s"Lunch: $name (${`type`}), price: $price"
}

object Lunch {

  sealed abstract class Dish

  case object MainDish extends Dish
  case object Starter extends Dish
  case object Dessert extends Dish
  case object Error extends Dish

  implicit val dishToString: StringConverter[Dish] = new StringConverter[Dish] {
    val mapping: Map[Dish, String] = Map(
      MainDish -> "main",
      Starter -> "starter",
      Dessert -> "dessert",
    )

    override def tryFrom(column: String): Try[Dish] = Try(mapping.map(_.swap)).map(_(column))

    override def to(dish: Dish): String = mapping.getOrElse(dish, "Error")
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