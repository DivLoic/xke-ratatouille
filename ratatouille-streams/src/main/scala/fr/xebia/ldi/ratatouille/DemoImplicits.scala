package fr.xebia.ldi.ratatouille

import java.util.Properties

import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
trait DemoImplicits {

  protected val logger: Logger = LoggerFactory.getLogger(getClass)

  implicit class javaMapOps(map: Map[_, _]) {
    def toProperties: Properties = {
      val props = new Properties()
      props.putAll(map.asInstanceOf[Map[Object, Object]].asJava)
      props
    }
  }

  val BreakfastLabel = "\uD83E\uDD50breakfast"
  val LunchLabel = "\uD83C\uDF55lunch"
  val DrinkLabel = "\uD83C\uDF7Adrink"
  val DinnerLabel = "\uD83C\uDF5Ddinner"
}
