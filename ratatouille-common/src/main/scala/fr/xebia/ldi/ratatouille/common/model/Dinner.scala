package fr.xebia.ldi.ratatouille.common.model

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.util.{Locale, UUID}

import com.sksamuel.avro4s.{AvroDoc, AvroName, AvroNamespace, RecordFormat}
import fr.xebia.ldi.ratatouille.common.model.Dinner.{Client, Command, Moment}
import org.apache.avro.generic.GenericRecord

/**
  * Created by loicmdivad.
  */
@AvroNamespace("ratatouille")
@AvroDoc("Food order corresponding to a dinner")
case class Dinner(@AvroDoc("Full name of the dish") dish: Command,
                  @AvroDoc("Customer information, null when take away")
                  @AvroName("client") maybeClient: Option[Client],
                  @AvroDoc("Region (format: Europe/Paris) and timestamp in Long") moment: Moment,
                  @AvroDoc("Time zone (format: CET)") zone: String) extends FoodOrder {

  override def toAvro: GenericRecord = RecordFormat[Dinner].to(this)

  override def toString: String = {

    val datetime = Instant
      .ofEpochSecond(moment.ts)
      .atZone(ZoneId.of(moment.region))
      .format(DateTimeFormatter.ofPattern("EEE d MMM yyyy hh:mm a", Locale.FRANCE))


    s"${dish.name}".padTo(70, " ").mkString("") + datetime + s" - ${moment.region}"
  }

}

object Dinner {

  case class Client(id: UUID) extends AnyVal

  case class Command(name: String, price: Double)

  case class Moment(region: String, ts: Long)

  val commands: Vector[Command] = Vector(
    Command("Mediterranean Mussels, lobster cream, fries", 29.00),
    Command("Salade Lyonnaise, haricot vert, pulled chicken breast, lardon", 39.00),
    Command("Tuna confit, lettuce, egg, haricots verts, olives, potato", 19.50),
    Command("Onion soup gratinée, emmental cheese", 22.50),
    Command("Baked cheese, garlic confit, rosemary", 23.50),
    Command("Quiche Lorraine: spinach, lardon, mixed green salad", 29.00),
    Command("Bourguignon Snail, garlic, parsley butter", 25.00),
    Command("Pan seared trout, lemon butter and almond sauce spinach", 26.50),
    Command("Duo of salmon, smoked & fresh, fine herbs", 28.00),
    Command("Croque Monsieur: toasted bread, ham, gruyere, béchamel ", 48.00),
    Command("Moroccan Couscous, grilled lamb merguez sausage", 22.50),
    Command("Roasted bone marrow, grilled mushrooms", 23.00),
    Command("Duck foie gras pat with pickled fruit, toasted baguette", 29.00),
    Command("Chopped raw steak, truffle cream, quail egg, arugula", 22.50),
    Command("Braised beef casserole, mashed potatoes, cheese gratin", 24.00),
    Command("Pan roasted pork chop, parsnips puree, sauce charcutiere", 31.00),
    Command("Braised beef stew, baby carrots, pearl onions, red wine jus", 39.50)
  )
}