name := "kafka-dev-tools"

version := "0.1"

scalaVersion := "2.12.10"

exportJars := true
// set the main class for packaging the main jar
mainClass in (Compile, packageBin) := Some("com.heap.kafkadevtools.ConsumeAvro")

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.2.2"
libraryDependencies += "org.apache.avro" % "avro" % "1.7.4"