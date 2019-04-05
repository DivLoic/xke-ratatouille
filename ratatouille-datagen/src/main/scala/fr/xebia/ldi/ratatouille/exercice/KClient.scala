package fr.xebia.ldi.ratatouille.exercice

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.ProducerMessage.Message
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import fr.xebia.ldi.ratatouille.model.Breakfast
import fr.xebia.ldi.ratatouille.serde.{BreakfastSerde, CirceSerializer}
import org.apache.kafka.common.serialization.{Serdes, Serializer}
import org.apache.kafka.common.utils.Bytes

/**
  * Created by loicmdivad.
  */
object KClient {

  implicit val stringSerializer: Serializer[String] = Serdes.String().serializer()

  implicit val bytesSerializer: Serializer[Bytes] = Serdes.Bytes().serializer()

  implicit val breakFastSerialiser: Serializer[Breakfast] = new BreakfastSerde().serializer()

  implicit val jsonSerialiser: CirceSerializer = new CirceSerializer()

  def setting[K, V](implicit actorSystem: ActorSystem,
                    key: Serializer[K],
                    value: Serializer[V]): ProducerSettings[K, V] = ProducerSettings(actorSystem, key, value)

  def producer[K, V](implicit actorSystem: ActorSystem,
                        key: Serializer[K],
                        value: Serializer[V]): RunnableGraph[ActorRef] =

    Source.actorRef[Message[K, V, NotUsed]](10, OverflowStrategy.dropBuffer)
      .via(Producer.flexiFlow(setting[K, V]))
      .to(Sink.ignore)




  val foo = Vector(1, 2, 3)
  val bar = Vector(1, 2, 3)

  val baz: Vector[Int] = foo.++:(bar)
}
