package fr.xebia.ldi.ratatouille.generate.http.model

/**
  * Created by loicmdivad.
  */
case class Command(message: String, exercise: Option[Exercise] = None)
