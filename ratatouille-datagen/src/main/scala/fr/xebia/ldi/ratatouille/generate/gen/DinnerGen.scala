package fr.xebia.ldi.ratatouille.generate.gen

import java.time.format.TextStyle
import java.time.{Instant, ZoneId}
import java.util.Locale

import fr.xebia.ldi.ratatouille.common.model.Dinner
import fr.xebia.ldi.ratatouille.common.model.Dinner.{Client, Moment}
import org.scalacheck.Gen.oneOf
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by loicmdivad.
  */
trait DinnerGen {

  def generate(zone: ZoneId): Gen[Dinner] = for {

    id <- Gen.uuid

    cmd <- Arbitrary(oneOf(Dinner.commands)).arbitrary

  } yield Dinner(
    cmd,
    Some(Client(id)),
    Moment(zone.getId, Instant.now().atZone(zone).toEpochSecond),
    zone.getDisplayName(TextStyle.SHORT, Locale.FRANCE)
  )
}
