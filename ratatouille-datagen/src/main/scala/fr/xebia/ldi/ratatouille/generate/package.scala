package fr.xebia.ldi.ratatouille

import java.util.Properties

import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try
import scala.collection.JavaConverters._

/**
  * Created by loicmdivad.
  */
package object generate {

  case class AppConfig(httpServer: HttpServerConfig, webApp: WebAppConfig, kafkaAdmin: KafkaAdminConfig)

  case class HttpServerConfig(host: String, port: Int)

  case class WebAppConfig(host: String, port: Int)

  case class KafkaAdminConfig(topics: Vector[KafkaTopicConfig])

  case class KafkaTopicConfig(override val name: String, partition: Int, replication: Short) extends
    NewTopic(name, partition, replication)

  case class KafkaClientConfig(bootstrap: Bootstrap)

  case class Bootstrap(servers: String)

  trait ConfigurableApp {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    def topicsCreation(kafkaConfig: KafkaClientConfig, appConfig: AppConfig): Try[Unit] = {
      val properties = kafkaProperties(kafkaConfig = kafkaConfig)
      val client: AdminClient = AdminClient.create(properties)
      val topics: Vector[NewTopic] = appConfig.kafkaAdmin.topics
      Try {
        client.createTopics(topics.asJava)
        client.close()
      }.map { _ =>
        topics.foreach(t => logger info s"topic creation: ${t.name} - partitions: ${t.numPartitions}")
      }
    }

    private def kafkaProperties(kafkaConfig: KafkaClientConfig): Properties = {
      val properties = new Properties()
      properties.putAll(Map("bootstrap.servers" -> kafkaConfig.bootstrap.servers).asJava)
      properties
    }
  }

}
