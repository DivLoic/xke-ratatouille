package fr.xebia.ldi.ratatouille.exercice

/**
  * Created by loicmdivad.
  */
object Event {

  sealed abstract class ActMessage

  case object Start extends ActMessage
  case object Stop extends ActMessage
  case object Status extends ActMessage

  case object Done extends ActMessage

  case object Send extends ActMessage
  case object Sent extends ActMessage

  sealed abstract class State extends ActMessage
  case object Standby extends State
  case object Running extends State
  case object Down extends State

}
