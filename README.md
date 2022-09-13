# kafka-dev-tools
- actually `a kafka-dev-tool` would be a more accurate title for now
- this is a small but might utility that allows you, as a human, to read binary avro messages for a given avsc (avro schema file in json format), from specified kafka brokers and topic.
- caveat emptor: this is a work in progress

### usage
```
SCHEMAFILE=</absolute/path/to/file.asvc> \
java -jar <path/to/kafka-dev-tools-assembly-0.1.jar> \
<brokersUrl:brokersPort> \
<topic_name> \
<consumer_group_name> \
<starting_position>
```

### for example (and these are current arg defaults)
```
SCHEMAFILE=</absolute/path/to/file.asvc> \
java -jar <path/to/kafka-dev-tools-assembly-0.1.jar> \
localhost:9092 \
account_properties \
lizTestConsumeAccountProperties \
latest
```

### to build
- git the project 
- cd `<project dir>`
- `sbt assembly`
