package fr.xebia.ldi.ratatouille.processor

import io.circe.Json
import org.apache.kafka.streams.kstream.ValueTransformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.metrics.stats.Min
import org.apache.kafka.common.metrics.{MeasurableStat, MetricConfig, Metrics, Sensor}
import org.apache.kafka.streams.StreamsMetrics
import org.apache.kafka.streams.processor.internals.metrics.CumulativeCount

import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
class WrongJsonSink extends ValueTransformer[Json, Unit] {

  var sensor: Sensor = _

  var context: ProcessorContext = _

  override def init(context: ProcessorContext): Unit = {
    this.context = context

    val streamsMetrics: StreamsMetrics = this.context.metrics

    val metricTags = Map("action" -> "none")

    val metricConfig = new MetricConfig().tags(metricTags.asJava)

    val metrics = new Metrics(metricConfig)

    metrics.sensor("start_ts")

    val metricName: MetricName = metrics.metricName(
      "json",
      "serdes-errors",
      "count of all failures related to json deserialization "
    )

    this.sensor = streamsMetrics.addSensor("start_ts", Sensor.RecordingLevel.INFO)

    sensor.add(metricName, new CumulativeCount())
  }

  override def transform(value: Json): Unit = {

    sensor.record()

  }

  override def close(): Unit = {}
}