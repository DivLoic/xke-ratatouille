package fr.xebia.ldi.ratatouille.generate

import fr.xebia.ldi.ratatouille.generate.actor.Event.ActMessage
import fr.xebia.ldi.ratatouille.generate.actor.{Event, ActorGen}


/**
  * Created by loicmdivad.
  */
class GeneratorPool(val pool: ActorGen*) {

  def selectByName(name: String): Option[ActorGen] = pool.find(_.name == name)

  def send(name: String, message: ActMessage): Option[ActorGen] = pool
    .find(_.name == name)
    .map{e => e ! message; e}
    .orElse(None)

  def broadcast(message: ActMessage): Unit = pool.foreach(_ ! Event.Status)
}

object GeneratorPool {

  val InputTopic = "input-food-order"
}
