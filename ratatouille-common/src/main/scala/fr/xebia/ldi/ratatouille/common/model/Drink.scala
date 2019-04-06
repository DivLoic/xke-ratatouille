package fr.xebia.ldi.ratatouille.common.model

import com.sksamuel.avro4s.AvroNamespace
import fr.xebia.ldi.ratatouille.common.codec
import fr.xebia.ldi.ratatouille.common.model.Drink.DrinkType
import scodec.codecs.{Discriminated, Discriminator}
import io.circe.generic.auto._
import io.circe.syntax._

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
case class Drink(name: String, `type`: DrinkType, quantity: Int, alcohol: Option[Double]) extends FoodOrder {

  override def toString: String = s"$name, ${quantity}cl.".padTo(60, " ").mkString("") +
    s"\t\t (${`type`.getClass.getSimpleName})"

}

object Drink {

  sealed trait DrinkType

  case class Wine() extends DrinkType
  case class Rhum() extends DrinkType
  case class Water() extends DrinkType
  case class Whisky() extends DrinkType
  case class Champagne() extends DrinkType

  object Wine extends Wine
  object Rhum extends Rhum
  object Water extends Water
  object Whisky extends Whisky
  object Champagne extends Champagne

  implicit val discriminated: Discriminated[DrinkType, Symbol] = Discriminated(codec.symbolCodec)

  object DrinkType {
    implicit val discriminator1: Discriminator[DrinkType, Wine, Symbol] = Discriminator('Wine)
    implicit val discriminator2: Discriminator[DrinkType, Rhum, Symbol] = Discriminator('Rhum)
    implicit val discriminator3: Discriminator[DrinkType, Water, Symbol] = Discriminator('Water)
    implicit val discriminator4: Discriminator[DrinkType, Whisky, Symbol] = Discriminator('Whisky)
    implicit val discriminator5: Discriminator[DrinkType, Champagne, Symbol] = Discriminator('Champagne)
  }

  val wine0 = Drink("Château Mouton Rothschild Pauillac", Wine, 15, Some(12.5))
  val wine1 = Drink("Château Lafite Rothschild Pauillac", Wine, 15, Some(12.5))
  val wine2 = Drink("Château Margaux", Wine, 15, Some(13.5))
  val wine3 = Drink("Château Latour Pauillac", Wine, 15, Some(13))
  val wine4 = Drink("Château Haut-Brion Pessac-Léognan", Wine, 15, Some(13))
  val wine5 = Drink("Pétrus Pomerol, Château Cheval Blanc Saint Émilion", Wine, 15, Some(13))
  val wine6 = Drink("Château d'Yquem Sauternes", Wine, 15, Some(14))

  val wines = Vector(wine0, wine1, wine2, wine3, wine4, wine5, wine6)

  val champagne0 = Drink("Moët & Chandon", Champagne, 150, Some(0))
  val champagne1 = Drink("Dom Pérignon", Champagne, 150, Some(0))
  val champagne2 = Drink("Mercier", Champagne, 150, Some(0))
  val champagne3 = Drink("Ruinart", Champagne, 150, Some(0))
  val champagne4 = Drink("Vve Clicquot", Champagne, 150, Some(0))
  val champagne5 = Drink("Krug", Champagne, 150, Some(0))

  val champagnes = Vector(champagne0, champagne1, champagne2, champagne3, champagne4, champagne5)

  val whisky1 = Drink("Talisker 18 Year Old", Whisky, 8, Some(45.8))
  val whisky2 = Drink("Glen Marnoch Speyside", Whisky, 8, Some(40))
  val whisky3 = Drink("Lagavulin 16 Year Old", Whisky, 8, Some(43))
  val whisky4 = Drink("Glen Scotia 18 Year Old", Whisky, 8, Some(46))
  val whisky5 = Drink("Glenmorangie Quinta Ruban 12 Year Old", Whisky, 8, Some(46))
  val whisky6 = Drink("Ardbeg An Oa", Whisky, 8, Some(46.6))
  val whisky7 = Drink("The Balvenie 12 Year Old Triple Cask", Whisky, 8, Some(40))
  val whisky8 = Drink("Aerstone Land Cask 10 Year Old", Whisky, 8, Some(40))
  val whisky9 = Drink("Glenfiddich Experimental Series - IPA Cask Finish", Whisky, 8, Some(43))
  val whisky10 = Drink("Loch Lomond 12 Year Old", Whisky, 8, Some(46))

  val whiskies = Vector(whisky1, whisky2, whisky3, whisky4, whisky5, whisky6, whisky7, whisky8, whisky9, whisky10)

  val tipunch = Drink("Ti-Punch", Rhum, 5, Some(45.0))
  val maiTai = Drink("Mai Tai", Rhum, 18, Some(45.0))
  val mojito = Drink("Mojito", Rhum, 18, Some(45.0))
  val gingerRhum = Drink("Ginger Rhum", Rhum, 18, Some(45.0))

  val rhums = Vector(tipunch, maiTai, mojito, gingerRhum)

  val flatWater = Drink("Evian", Water, 28, None)
  val sparklingWater = Drink("San Pellegrino", Water, 28, None)

  val waters = Vector(flatWater, sparklingWater)
}
