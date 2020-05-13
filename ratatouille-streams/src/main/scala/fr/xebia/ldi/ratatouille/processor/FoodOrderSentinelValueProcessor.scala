package fr.xebia.ldi.ratatouille.processor

import java.util.concurrent.TimeUnit

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.metrics.Sensor
import org.apache.kafka.common.metrics.stats.{Count, Rate, Total}
import org.apache.kafka.streams.kstream.ValueTransformer
import org.apache.kafka.streams.processor.ProcessorContext

import scala.collection.JavaConverters._

/**
 * Created by loicmdivad.
 */
class FoodOrderSentinelValueProcessor extends ValueTransformer[FoodOrder, Unit] {

  var sensor: Sensor = _
  var context: ProcessorContext = _

  def metricName(stat: String) =
    new MetricName(
      s"sentinel-value-$stat",

      "ratatouille-custom-metrics",

      "Track the food orders deserialization failure",

      Map("app_id" -> context.applicationId(), "task_id" -> context.taskId().toString).asJava
    )

  override def init(context: ProcessorContext): Unit = {
    this.context = context

    sensor = this.context.metrics.addSensor(
      s"sentinel-value-${context.taskId().toString}",
      Sensor.RecordingLevel.INFO
    )

    sensor.add(metricName("total"), new Total)
    sensor.add(metricName("rate"), new Rate(TimeUnit.SECONDS, new Count()))
  }

  override def transform(value: FoodOrder): Unit = sensor.record()

  override def close(): Unit = {}
}