[中文版](readme_zh.md)

## flink-connector-mqtt 

Implemented based on the latest [FLIP-27](https://cwiki.apache.org/confluence/display/FLINK/FLIP-27%3A+Refactor+Source+Interface) architecture of MQTT connector for Flink. The main features are as follows:

- Compatible with the latest Flink version (1.17.1).
- Supports reading data from multiple topics simultaneously and automatically shards based on topics.
- Uses a high-performance MQTT client (hivemq-mqtt).
- Supports querying in Flink SQL style.

## Dependency Description

- Must use JDK 17 or higher.
- Currently only supports MQTT 3 protocol, MQTT 5 will be supported in the future.
- Flink version supports 1.17.1 and above.

## Usage

1. Add the dependency:

```xml
<dependency>
   <groupId>com.github.davidfantasy</groupId>
   <artifactId>flink-connector-mqtt</artifactId>
   <version>1.1.0</version>
</dependency>
```

2. Example code for using as a streaming data source:

```java
public class MqttSourceTest {

   public static void main(String[] args) throws Exception {
       StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
       env.setParallelism(1);
       MqttProperties mqttProp = new MqttProperties();
       mqttProp.setHost("broker-cn.emqx.io");
       mqttProp.setPort(1883);
//        mqttProp.setUsername("");
//        mqttProp.setPassword("");
       List<MqttTopic> topics = new ArrayList<>();
       topics.add(new MqttTopic("/flink-connector/mqtt/source/test", 0));
       var source = env.fromSource(new MqttSource(mqttProp, topics), WatermarkStrategy.noWatermarks(), "Mqtt Source");
       source.map(v -> {
           var msg = (MqttMessage) v;
           return msg.getTopic() + ":" + new String(msg.getPayload());
       }).print();
       env.execute("MQTT Source Test");
   }

}
```

Create a table in Flink SQL:

```sql
CREATE TABLE mqttTest (
    id INTEGER,
    code STRING
) WITH (
    'connector' = 'mqtt',
    'server' = 'broker-cn.emqx.io',
    'port' = '1883',
    'topic' = '/flink-connector/mqtt/source/test'
)
```

**Note**: When using Flink SQL, the message format for MQTT must be in JSON format. The JSON format corresponding to the table structure above is:

```json
{"id":123,"code":"some hello"}
```

Currently, the following configurations can be used in the table:

- connector: Fixed as mqtt
- server: MQTT broker host, required
- port: MQTT broker port, required
- username: Authentication username, optional
- password: Authentication password, optional
- topic: MQTT topic corresponding to this table, required
- qos: Quality of Service level for subscription, optional, default is 0