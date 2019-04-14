package fr.xebia.ldi.ratatouille.processor

import fr.xebia.ldi.ratatouille.common.model.FoodOrder
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.metrics.Sensor
import org.apache.kafka.common.metrics.stats.{Count, Rate, Sum}
import org.apache.kafka.streams.kstream.ValueTransformer
import org.apache.kafka.streams.processor.ProcessorContext

import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
class FoodOrderErrorSink extends ValueTransformer[FoodOrder, Unit] {

  var sensor: Sensor = _

  var context: ProcessorContext = _

  def metricName(stat: String) = new MetricName(
    s"food-errors-$stat",
    "custom-metrics",
    "Stats related to the food command deserialization failure",
    Map("app_id" -> context.applicationId(), "task_id" -> context.taskId().toString).asJava
  )

  override def init(context: ProcessorContext): Unit = {
    this.context = context

    sensor = this.context.metrics.addSensor("food-errors", Sensor.RecordingLevel.INFO)

    sensor.add(metricName("count"), new Count())
    sensor.add(metricName("rate"), new Rate())
    sensor.add(metricName("sum"), new Sum())
  }

  override def transform(value: FoodOrder): Unit = {

    sensor.record()

  }

  override def close(): Unit = {}
}