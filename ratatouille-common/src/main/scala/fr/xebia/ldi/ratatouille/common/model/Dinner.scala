package fr.xebia.ldi.ratatouille.common.model

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.{Locale, UUID}

import com.sksamuel.avro4s.{AvroName, AvroNamespace, RecordFormat}
import fr.xebia.ldi.ratatouille.common.model.Dinner.{Client, Command, Moment}
import org.apache.avro.generic.GenericRecord

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
case class Dinner(dish: Command,
                  @AvroName("client") maybeClient: Option[Client],
                  moment: Moment,
                  zone: String) extends FoodOrder {

  override def toAvro: GenericRecord = RecordFormat[Dinner].to(this)

  override def toString: String =
    s"${dish.name}".padTo(100, " ").mkString("") + DateTimeFormatter.ofPattern("EEE d MMM yyyy h:mm a", Locale.FRANCE)
      .withZone(ZoneId.of(moment.region)).format(Instant.ofEpochMilli(moment.ts)) + s" - ${moment.region}"

}

object Dinner {

  case class Client(id: UUID) extends AnyVal

  case class Command(name: String, price: Double)

  case class Moment(region: String, ts: Long)

  val commands: Vector[Command] = Vector(
    Command("Choucroute de la mer - Bar, haddock, saumon d'Ecosse, Langoustine, beurre blanc", 29.00),
    Command("Homard canadien entier rôti au beurre demi sel - Sauce béarnaise, frites maison", 39.00),
    Command("Merlan frit façon fish & chips - Sauce tartare, frites maison", 19.50),
    Command("Tartare de saumon d'Ecosse - Concombre, mesclun, frites maison", 22.50),
    Command("Aile de raie à la grenobloise - Petits légumes, purée de pommes de terre maison", 23.50),
    Command("Filet de bar rôti - Émulsion coquillages, riz croustillant aux herbes, artichaut poivrade", 29.00),
    Command("Gambas et couteaux en persillade - Sauce vierge, purée de pommes de terre maison", 25.00),
    Command("Thon mi-cuit - Sauce vierge aux coques, fine ratatouille", 26.50),
    Command("Poulpe grillé en persillade - Crème d'olives Kalamata Kalios, purée de pommes de terre maison", 28.00),
    Command("Belle sole grillée ou meunière - Env 400g, selon arrivage, haricots verts", 48.00),
    Command("Suprême de volaille jaune de vendée - Crème de morilles, légumes de saison", 22.50),
    Command("Foie de veau poêlé en persillade - Purée de pommes de terre maison", 23.00),
    Command("Magret de canard rôti - Pois gourmands, champignons, pommes grenaille", 29.00),
    Command("Tartare de boeuf charolais aux couteaux - Frites maison, salade verte", 22.50),
    Command("Carpaccio de bœuf Charolais du Grand Café - Émincé de bœuf, crème mascarpone et champignons ", 24.00),
    Command("Entrecôte grillée (env 300g) - Sauce béarnaise, frites maison", 31.00),
    Command("Filet de boeuf (env 200g) - Au poivre flambé en salle ou béarnaise, frites maison", 39.50)
  )
}
