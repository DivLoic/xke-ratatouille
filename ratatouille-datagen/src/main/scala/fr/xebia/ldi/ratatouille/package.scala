package fr.xebia.ldi

import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by loicmdivad.
  */
package object ratatouille {

  case class AppConfig(httpServer: HttpServerConfig, kafkaAdmin: KafkaAdminConfig)

  case class HttpServerConfig(host: String, port: Int)

  case class KafkaAdminConfig(topics: Vector[KafkaTopicConfig])

  case class KafkaTopicConfig(override val name: String, partition: Int, replication: Short) extends
    NewTopic(name, partition, replication)

  case class KafkaClientConfig(bootstrap: Bootstrap)
  case class Bootstrap(servers: String)


  trait ConfigurableApp {

    private val logger: Logger = LoggerFactory.getLogger(getClass)

    def topicsCreation(kafkaConfig: KafkaClientConfig, appConfig: AppConfig) = {
      val properties = Map[String, AnyRef]("bootstrap.servers" -> kafkaConfig.bootstrap.servers)
      val client: AdminClient = AdminClient.create(properties.asJava)
      val topics: Vector[NewTopic] = appConfig.kafkaAdmin.topics
      Try {
        client.createTopics(topics.asJava)
        client.close()
      }.map { _ =>
        topics.foreach(t => logger info s"topic creation: ${t.name} - partitions: ${t.numPartitions}")
      }
    }
  }

}
