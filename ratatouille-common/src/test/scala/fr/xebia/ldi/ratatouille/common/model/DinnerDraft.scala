package fr.xebia.ldi.ratatouille.common.model

import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

import scodec.{Attempt, Err, codecs}
import scodec.codecs.implicits._
import scodec.codecs._
import scodec.bits._
import scodec.codecs.cstring

/**
  * Created by loicmdivad.
  */
object DinnerDraft extends App {

  val chunk = cstring.decode(???).require.value

  println(s"Frame = $chunk")
  println()
  println(ZoneId.SHORT_IDS.get(chunk))

}
