package fr.xebia.ldi.ratatouille.handler

import java.nio.charset.StandardCharsets.UTF_8
import java.util

import fr.xebia.ldi.ratatouille.DemoImplicits
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.streams.errors.DeserializationExceptionHandler
import org.apache.kafka.streams.errors.DeserializationExceptionHandler.DeserializationHandlerResponse
import org.apache.kafka.streams.processor.ProcessorContext
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
 * Created by loicmdivad.
 */
class DeadLetterQueueFoodExceptionHandler() extends DeserializationExceptionHandler with DemoImplicits {

  var topic: String = _
  var producer: KafkaProducer[Array[Byte], Array[Byte]] = _

  val logger: Logger = LoggerFactory.getLogger(getClass)

  override def configure(configs: util.Map[String, _]): Unit = {

    topic = configs.asScala.get("dlq.topic.name").map(_.toString).orNull

    val properties = (configs.asScala.toMap - "dlq.topic.name")

      .filterKeys(_.startsWith("dlq."))

      .map { case (k: String, v) => (k.substring(4), v) }

    producer = new KafkaProducer[Array[Byte], Array[Byte]](properties)
  }

  override def handle(context: ProcessorContext,
                      record: ConsumerRecord[Array[Byte], Array[Byte]],
                      exception: Exception): DeserializationHandlerResponse = {

    val headers = record.headers().toArray ++ Array[Header](
      new RecordHeader("message", exception.getMessage.getBytes(UTF_8)),
      new RecordHeader("hexavalue", record.value.toHex.getBytes(UTF_8)),
      new RecordHeader("stacktrace", exception.getStackTrace.flatMap(_.toString.getBytes(UTF_8)))
    ) toIterable

    val producerRecord = new ProducerRecord(topic, null, record.timestamp, record.key, record.value, headers.asJava)

    producer.send(
      producerRecord,
      (_: RecordMetadata, exception: Exception) => Option(exception).foreach(logger.warn("[DLQ] - ", _))
    )

    DeserializationHandlerResponse.CONTINUE
  }
}
