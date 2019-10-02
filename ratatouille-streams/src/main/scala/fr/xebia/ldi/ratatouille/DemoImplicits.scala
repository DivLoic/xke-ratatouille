package fr.xebia.ldi.ratatouille

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.streams.StreamsConfig
import scodec.bits.BitVector

import scala.collection.JavaConverters._
import scala.language.implicitConversions

/**
 * Created by loicmdivad.
 */
trait DemoImplicits {

  val `🥐Label` = "\uD83E\uDD50breakfast"
  val `🍕Label` = "\uD83C\uDF55lunch"
  val `🍺Label` = "\uD83C\uDF7Adrink"
  val `🍝Label` = "\uD83C\uDF5Ddinner"

  val monitoringConfigs: Map[String, String] = Map(
    StreamsConfig.PRODUCER_PREFIX + ProducerConfig.INTERCEPTOR_CLASSES_CONFIG ->
      "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor",
    StreamsConfig.CONSUMER_PREFIX + ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG ->
      "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor"
  )

  // just to simplify the live coding, sorry 🙏
  implicit def mapToProperties(map: Map[_, _]): Properties = {
    val props = new Properties()
    props.putAll(map.asInstanceOf[Map[AnyRef, AnyRef]].asJava)
    props
  }

  implicit class ByteArrayOps(array: Array[Byte]) {
    def toHex: String = BitVector(array).toHex
    def toHexStr: Array[Byte] = array.flatMap(BitVector(_).toHex.getBytes(UTF_8))
  }
}
