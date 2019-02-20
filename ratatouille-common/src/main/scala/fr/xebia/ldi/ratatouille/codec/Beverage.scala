package fr.xebia.ldi.ratatouille.codec

import cats.kernel.Monoid
import fr.xebia.ldi.ratatouille.codec.BeverageType._

/**
  * Created by loicmdivad.
  */
case class Beverage(name: String, drink: BeverageType, quantity: Int, alcohol: Option[Double])

object Beverage {

  case class BeverageCommand(beverages: Vector[Beverage])

  implicit val beverageMonoid: Monoid[BeverageCommand] = new Monoid[BeverageCommand]{

    override def empty: BeverageCommand = IncorrectBeverageCommand

    override def combine(x: BeverageCommand, y: BeverageCommand): BeverageCommand =
      BeverageCommand( x.beverages ++: y.beverages)
  }

  object IncorrectBeverageCommand extends BeverageCommand(Vector.empty)

  val wine0 = Beverage("Château Mouton Rothschild Pauillac", Wine, 15, Some(12.5))
  val wine1 = Beverage("Château Lafite Rothschild Pauillac", Wine, 15, Some(12.5))
  val wine2 = Beverage("Château Margaux", Wine, 15, Some(13.5))
  val wine3 = Beverage("Château Latour Pauillac", Wine, 15, Some(13))
  val wine4 = Beverage("Château Haut-Brion Pessac-Léognan", Wine, 15, Some(13))
  val wine5 = Beverage("Pétrus Pomerol, Château Cheval Blanc Saint Émilion", Wine, 15, Some(13))
  val wine6 = Beverage("Château d'Yquem Sauternes", Wine, 15, Some(14))

  val wines = Vector(wine0, wine1, wine2, wine3, wine4, wine5, wine6)

  val champagne0 = Beverage("Moët & Chandon", Champagne, 150, Some(0))
  val champagne1 = Beverage("Dom Pérignon", Champagne, 150, Some(0))
  val champagne2 = Beverage("Mercier", Champagne, 150, Some(0))
  val champagne3 = Beverage("Ruinart", Champagne, 150, Some(0))
  val champagne4 = Beverage("Vve Clicquot", Champagne, 150, Some(0))
  val champagne5 = Beverage("Krug", Champagne, 150, Some(0))

  val champagnes = Vector(champagne0, champagne1, champagne2, champagne3, champagne4, champagne5)

  val whisky1 = Beverage("Talisker 18 Year Old", Whisky, 75, Some(45.8))
  val whisky2 = Beverage("Glen Marnoch Speyside", Whisky, 75, Some(40))
  val whisky3 = Beverage("Lagavulin 16 Year Old", Whisky, 75, Some(43))
  val whisky4 = Beverage("Glen Scotia 18 Year Old", Whisky, 75, Some(46))
  val whisky5 = Beverage("Glenmorangie Quinta Ruban 12 Year Old", Whisky, 75, Some(46))
  val whisky6 = Beverage("Ardbeg An Oa", Whisky, 75, Some(46.6))
  val whisky7 = Beverage("The Balvenie 12 Year Old Triple Cask", Whisky, 75, Some(40))
  val whisky8 = Beverage("Aerstone Land Cask 10 Year Old", Whisky, 75, Some(40))
  val whisky9 = Beverage("Glenfiddich Experimental Series - IPA Cask Finish", Whisky, 75, Some(43))
  val whisky10 = Beverage("Loch Lomond 12 Year Old", Whisky, 75, Some(46))

  val whiskies = Vector(whisky1, whisky2, whisky3, whisky4, whisky5, whisky6, whisky7, whisky8, whisky9, whisky10)

  val tipunch = Beverage("Ti-Punch", Rhum, 5, Some(45.0))

}
