import fr.xebia.ldi.ratatouille.codec._
implicit val appAttempt = Worksheet.AttemptA()
implicit val bf2Codec = Worksheet.CodecB()




import cats.Applicative
import scodec.bits.BitVector
import scodec.{Attempt, Codec}
import scodec.codecs._
import scodec.codecs.implicits._

case class Breakfast2(lang: Lang,
                      drink: Drink,
                      fruit: Fruit,
                      dishes: Either[Meat, Vector[Pastry]])

case class Breakfast1(lang: Lang,
                      drink: Drink,
                      fruit: Fruit,
                      dishes: Vector[Pastry] = Vector.empty)

val frame1: Array[Byte] = Array(0x33, 0xd4, 0xfc, 0x00, 0x00, 0x00, 0x01, 0xa5).map(_.toByte)

val frame2: Array[Byte] = Array(0x44, 0xd2, 0xfe, 0x10, 0x02, 0x03, 0x01).map(_.toByte)

Codec.decode[Breakfast2](BitVector(frame1))

Codec.decode[Breakfast2](BitVector(frame2))

