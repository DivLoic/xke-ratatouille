package fr.xebia.ldi.ratatouille.generate.http

import fr.xebia.ldi.ratatouille.generate.GeneratorPool
import fr.xebia.ldi.ratatouille.generate.actor.Event.{Down, Running}
import fr.xebia.ldi.ratatouille.generate.http.model.{Command, Exercise, Status}

/**
  * Created by loicmdivad.
  */
trait Service {

  implicit val exercisesPool: GeneratorPool

  def getStatus =  Status(
    message = "OK!",
    code = 1,
    exercises = exercisesPool.pool.toVector.map(ex =>
    Exercise(ex.name, ex.getStatus match {
      case Running => true
      case _ => false
    }))
  )

  def startOne(name: String) = exercisesPool
    .pool
    .find(_.name == name)
    .map(ex => (ex, ex.start))
    .map { target =>
      val result = Exercise(target._1.name, target._1.getStatus match {
        case Running => true
        case _ => false
      })
      target._2 match{
        case Down => Command(s"Failed to start exercise: $name", Some(result))
        case _ => Command(s"Started exercise $name with success", Some(result))
      }
    }.getOrElse(Command(s"No such exercise: $name"))

  def stopOne(name: String) = exercisesPool
    .pool
    .find(_.name == name)
    .map(ex => (ex, ex.stop))
    .map { target =>
      val result = Exercise(target._1.name, target._1.getStatus match {
        case Running => true
        case _ => false
      })
      target._2 match{
        case Down => Command(s"Failed to stop exercise: $name", Some(result))
        case _ => Command(s"Stop exercise $name with success", Some(result))
      }
    }.getOrElse(Command(s"No such exercise: $name"))
}
