package fr.xebia.ldi.ratatouille.codec

import fr.xebia.ldi.ratatouille.codec.Dinner.{Client, Command, Moment}

/**
  * Created by loicmdivad.
  */
case class Dinner(dish: Command, mayBeClient: Option[Client], moment: Moment) extends FoodOrder

object Dinner {

  case class Client(id: Long) extends AnyVal

  case class Command(name: String, price: Double)

  case class Moment(ts: Long, zone: String)

}
