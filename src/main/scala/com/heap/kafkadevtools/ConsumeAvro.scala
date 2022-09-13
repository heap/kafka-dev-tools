package com.heap.kafkadevtools

import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer, ConsumerConfig => KafkaConsumerConfig}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.Properties
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

/*
USAGE: SCHEMAFILE=</absolute/path/to/file.asvc> \
java -jar <path/to/kafka-dev-tools-assembly-0.1.jar> \
localhost:9092 \
account_properties \
lizTestConsumeAccountProperties \
latest
 */

object ConsumeAvro extends App {

  val config: ConsumerConfig = parseConfigFromArgs(args)

  val consumer: KafkaConsumer[String, GenericRecord] =
    new KafkaConsumer[String, GenericRecord](configuration)
  println(s"***---- Today's Topics ----***")
  consumer.listTopics.asScala foreach (t => println(s"topic: $t"))

  println(s"***---- subscribing to ${config.topic} as consumergroup ${config.groupId} ----***")
  consumer.subscribe(List(config.topic).asJava)

  println(s"***---- receiving... ----***")
  println(s"***---- (ctrl+c to stop the torrent) ----***")
  receiveMessages()

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  private def configuration: Properties = {
    val props = new Properties()
    // brokers, groupId and offsets
    props.put(KafkaConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.brokers)
    props.put(KafkaConsumerConfig.GROUP_ID_CONFIG, config.groupId)
    props.put(KafkaConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.startingPosition)
    // serde
    props.put(
      KafkaConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      classOf[StringDeserializer].getName
    )
    props.put(
      KafkaConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      classOf[GenericRecordDeserializer].getName
    )
    // security
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL")
    props
  }

  def receiveMessages(): Unit = {
    while (true) {
      Try {
        consumer.poll(Duration.ofMillis(10000))
      } match {
        case Success(records) =>
          println(s"SUCCESS! found ${records.asScala.size}")
          records.asScala.foreach(
            (record: ConsumerRecord[String, GenericRecord]) => {
              val thing = record.value()
              println(s"Received message: ${thing}")
            }
          )
        case Failure(exception) => println(s"Darn it: failed because ${exception.getMessage}")
      }
    }
  }

  def parseConfigFromArgs(args: Array[String]): ConsumerConfig = {
    ConsumerConfig(
      brokers = Try(args(0)).getOrElse("localhost:9092"),
      topic = Try(args(1)).getOrElse("account_properties"),
      groupId = Try(args(2)).getOrElse("lizTestConsumeAccountProperties"),
      startingPosition = Try(args(3)).getOrElse("latest")
    )
  }
}

final case class ConsumerConfig(
                                 brokers: String,
                                 topic: String,
                                 groupId: String,
                                 startingPosition: String
                               )
