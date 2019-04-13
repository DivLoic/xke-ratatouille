package fr.xebia.ldi.ratatouille.common.model

import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

import scodec.codecs
import scodec.codecs.cstring

/**
  * Created by loicmdivad.
  */
object DinnerDraft extends App {

  println(cstring.encode("AET").require.toHex)

  println(ZoneId.of(ZoneId.SHORT_IDS.get("AET")).getId)
  println(ZoneId.of(ZoneId.SHORT_IDS.get("AET")).getDisplayName(TextStyle.SHORT, Locale.FRENCH))

  println(ZoneId.of(ZoneId.SHORT_IDS.get("AET")).getDisplayName(TextStyle.FULL_STANDALONE, Locale.FRENCH))

  println(codecs.utf8.encode("Côte Est (Nouvelle-Galles du Sud)").require.toHex)

}
