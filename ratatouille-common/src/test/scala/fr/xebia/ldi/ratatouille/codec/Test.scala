package fr.xebia.ldi.ratatouille.codec

import purecsv.safe._

/**
  * Created by loicmdivad.
  */
object Test extends App {

  val foo = CSVReader[Lunch].readCSVFromString("\"Bavette de veau au saté - pommes grenailles - choux de Bruxelles (+4€)\",19.0,main dish", ',')

  println(foo)

}
