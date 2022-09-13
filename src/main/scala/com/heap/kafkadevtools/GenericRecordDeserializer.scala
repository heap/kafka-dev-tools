package com.heap.kafkadevtools

import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.kafka.common.serialization.Deserializer

import java.io.File

class GenericRecordDeserializer extends Deserializer[GenericRecord] {
  val schemaFile: String = sys.env.getOrElse("SCHEMAFILE","")
  val schema: org.apache.avro.Schema = new Schema.Parser().parse(new File(schemaFile))

  override def deserialize(topic: String, data: Array[Byte]): GenericRecord = {
    def read(bytes: Array[Byte],
             schema: org.apache.avro.Schema): org.apache.avro.generic.GenericRecord = {
      import org.apache.avro.file.SeekableByteArrayInput
      import org.apache.avro.generic.{GenericDatumReader, GenericRecord}

      val datumReader = new GenericDatumReader[GenericRecord](schema)
      val inputStream = new SeekableByteArrayInput(bytes)
      val decoder = DecoderFactory.get.binaryDecoder(inputStream, null)
      if (decoder.isEnd) null
      else
        datumReader.read(null,decoder)
    }
    read(data, schema)
  }

}
