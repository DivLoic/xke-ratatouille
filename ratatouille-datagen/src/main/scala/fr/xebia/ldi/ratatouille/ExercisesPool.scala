package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.exercice.Event.ActMessage
import fr.xebia.ldi.ratatouille.exercice.{Event, Exercise}


/**
  * Created by loicmdivad.
  */
class ExercisesPool(val pool: Vector[Exercise]) {

  def selectByName(name: String): Option[Exercise] = pool.find(_.name == name)

  def send(name: String, message: ActMessage): Option[Exercise] = pool
    .find(_.name == name)
    .map{e => e ! message; e}
    .orElse(None)

  def broadcast(message: ActMessage): Unit = pool.foreach(_ ! Event.Status)
}
