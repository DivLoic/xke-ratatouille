package fr.xebia.ldi.ratatouille.http.model

/**
  * Created by loicmdivad.
  */
case class Command(message: String, exercise: Option[Exercise] = None)
