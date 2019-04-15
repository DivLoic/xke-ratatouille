package fr.xebia.ldi.ratatouille.handler

import java.util

import com.sksamuel.avro4s.RecordFormat
import fr.xebia.ldi.ratatouille.handler.DeadLetterQueueFoodExceptionHandler.{DLQMessage, dqlFormat, recordToAvro}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.streams.errors.DeserializationExceptionHandler
import org.apache.kafka.streams.errors.DeserializationExceptionHandler.DeserializationHandlerResponse
import org.apache.kafka.streams.processor.ProcessorContext
import scodec.bits.BitVector

import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
class DeadLetterQueueFoodExceptionHandler() extends DeserializationExceptionHandler {

  var topic: String = _
  var producer: KafkaProducer[Array[Byte], GenericRecord] = _

  override def handle(context: ProcessorContext,
                      record: ConsumerRecord[Array[Byte], Array[Byte]],
                      exception: Exception): DeserializationHandlerResponse = {

    val valueMessage: GenericRecord = recordToAvro(record, exception)

    producer.send(new ProducerRecord(topic, null, record.timestamp, record.key, valueMessage))

    DeserializationHandlerResponse.CONTINUE
  }

  override def configure(configs: util.Map[String, _]): Unit = {
    topic = configs.asScala.get("dlq.topic.name").map(_.toString).orNull

    producer = new KafkaProducer[Array[Byte], GenericRecord]((configs.asScala - "dlq.topic.name")

      .filter { case (key, _) => key.startsWith("dlq.") }

      .map { case (key, value) => key.toLowerCase.substring(4) -> value.asInstanceOf[AnyRef] }.asJava)

  }
}

object DeadLetterQueueFoodExceptionHandler {

  implicit val dqlFormat: RecordFormat[DLQMessage] = RecordFormat[DLQMessage]

  def recordToAvro(record: ConsumerRecord[Array[Byte], Array[Byte]], exception: Exception): GenericRecord = dqlFormat
    .to(
      DLQMessage(
      record.value,
      BitVector(record.value).toHex,
      exception.getMessage,
      exception.getStackTrace.toVector.map(_.toString)
      )
    )

  case class DLQMessage(origin: Array[Byte], hexa: String, message: String, traces: Vector[String])
}
