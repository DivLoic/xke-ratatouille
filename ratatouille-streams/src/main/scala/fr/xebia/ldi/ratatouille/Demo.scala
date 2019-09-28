package fr.xebia.ldi.ratatouille

import fr.xebia.ldi.ratatouille.common.model._
import fr.xebia.ldi.ratatouille.common.serde.FoodOrderSerde
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.{LogAndContinueExceptionHandler, LogAndFailExceptionHandler}
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import fr.xebia.ldi.ratatouille.handler.DeadLetterQueueFoodExceptionHandler
import fr.xebia.ldi.ratatouille.processor.FoodOrderSentinelValueProcessor
import fr.xebia.ldi.ratatouille.serde.SentinelValueSerde
import fr.xebia.ldi.ratatouille.serde.SentinelValueSerde.FoodOrderError
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
  * Created by loicmdivad.
  */
object Demo extends App with DemoImplicits {

}
