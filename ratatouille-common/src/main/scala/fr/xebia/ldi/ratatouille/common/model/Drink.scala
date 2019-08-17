package fr.xebia.ldi.ratatouille.common.model

import com.sksamuel.avro4s.{AvroNamespace, RecordFormat}
import fr.xebia.ldi.ratatouille.common.codec
import fr.xebia.ldi.ratatouille.common.model.Drink.DrinkType
import org.apache.avro.generic.GenericRecord
import scodec.codecs.{Discriminated, Discriminator}

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
case class Drink(name: String, `type`: DrinkType, quantity: Int, alcohol: Option[Double]) extends FoodOrder {

  override def toAvro: GenericRecord = RecordFormat[Drink].to(this)

  override def toString: String = s"$name, ${quantity}cl.".padTo(60, " ").mkString("") + s"\t\t (${`type`})"

}

object Drink {

  sealed trait DrinkType

  case object Wine extends DrinkType
  case object Rhum extends DrinkType
  case object Water extends DrinkType
  case object Whisky extends DrinkType
  case object Champagne extends DrinkType

  implicit val discriminated: Discriminated[DrinkType, Symbol] = Discriminated(codec.symbolCodec)

  object DrinkType {
    implicit val discriminator1: Discriminator[DrinkType, Wine.type, Symbol] = Discriminator(Symbol("Wine"))
    implicit val discriminator2: Discriminator[DrinkType, Rhum.type, Symbol] = Discriminator(Symbol("Rhum"))
    implicit val discriminator3: Discriminator[DrinkType, Water.type, Symbol] = Discriminator(Symbol("Water"))
    implicit val discriminator4: Discriminator[DrinkType, Whisky.type, Symbol] = Discriminator(Symbol("Whisky"))
    implicit val discriminator5: Discriminator[DrinkType, Champagne.type, Symbol] = Discriminator(Symbol("Champagne"))
  }

  val wine0 = Drink("Pauillac, Château Mouton Rothschild", Wine, 15, Some(12.5))
  val wine1 = Drink("Pauillac, Château Lafite Rothschild", Wine, 15, Some(12.5))
  val wine2 = Drink("Carruades de Lafite (Mathusalem)", Wine, 600, Some(14))
  val wine3 = Drink("Margaux, Château Margaux", Wine, 15, Some(13.5))
  val wine4 = Drink("Château Latour", Wine, 75, Some(13))
  val wine5 = Drink("Les Forts de Latour (Magnum)", Wine, 150, Some(13))
  val wine6 = Drink("Pauillac, Château Latour", Wine, 15, Some(13))
  val wine7 = Drink("Volnay, Château de Savigny-lès-Beaune", Wine, 75, Some(12.5))
  val wine8 = Drink("Volnay, Domaine Lois Boillot", Wine, 15, Some(13))
  val wine9 = Drink("Bourgogne, Domaine Armand Rousseau", Wine, 15, Some(12.5))
  val wine10 = Drink("Beaujolai, Côte de Brouilly", Wine, 75, Some(13.5))
  val wine11 = Drink("Chablis, Bernard de Paix", Wine, 15, Some(12.5))
  val wine12 = Drink("Chablis, Bernard de Paix (Balthazar)", Wine, 1200, Some(12.5))
  val wine13 = Drink("Côtes du Rhône", Wine, 75, Some(12.5))
  val wine14 = Drink("Château Neuf Du Pape", Wine, 15, Some(12.5))
  val wine15 = Drink("Saint-Péray (Magnum)", Wine, 150, Some(12.5))
  val wine16 = Drink("Crozes-Hermitage (Magnum)", Wine, 150, Some(12.5))
  val wine17 = Drink("Pessac-Léognan, Château Haut-Brion", Wine, 15, Some(13))
  val wine18 = Drink("Pétrus Pomerol, Château Cheval Blanc Saint Émilion", Wine, 15, Some(13))
  val wine19 = Drink("Sauternes, Château d'Yquem", Wine, 15, Some(14))
  val wine20 = Drink("Sauternes, Château d'Yquem", Wine, 75, Some(14))

  val wines = Vector(
    wine0, wine1, wine2, wine3, wine4, wine5, wine6, wine7, wine8, wine9, wine10,
    wine11, wine12, wine13, wine14, wine15, wine16, wine17, wine18, wine19, wine20,
  )

  val champagne0 = Drink("Moët & Chandon", Champagne, 150, Some(0))
  val champagne1 = Drink("Dom Pérignon", Champagne, 150, Some(0))
  val champagne2 = Drink("Mercier", Champagne, 150, Some(0))
  val champagne3 = Drink("Ruinart", Champagne, 150, Some(0))
  val champagne4 = Drink("Vue Clicquot", Champagne, 150, Some(0))
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
