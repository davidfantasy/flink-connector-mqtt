## flink-mqtt-connector

基于flink最新的[FLIP-27](https://cwiki.apache.org/confluence/display/FLINK/FLIP-27%3A+Refactor+Source+Interface)架构对MQTT
connector的实现，主要特性如下：

- 兼容最新的flink版本（1.17.1）
- 支持多个topic同时读取数据，并基于topic进行自动分片
- 使用了高性能的MQTT客户端（hivemq-mqtt）

## 依赖说明

- 必须使用JDK 17及以上版本
- 目前暂时只支持MQTT 3协议，后续会支持MQTT 5
- flink版本支持1.17.1及以上版本

## 使用方法

1. 引入依赖
maven:
```xml
<dependency>
   <groupId>com.github.davidfantasy.flink.connector.mqtt</groupId>
   <artifactId>flink-connector-mqtt</artifactId>
   <version>1.0.0</version>
</dependency>
```
gradle:
```gradle
implementation 'com.github.davidfantasy.flink.connector.mqtt:flink-connector-mqtt:1.0.0
```
2. 示例代码：

```java
public class MqttSourceTest {

   public static void main(String[] args) throws Exception {
      StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
      env.setParallelism(1);
      MqttProperties mqttProp = new MqttProperties();
      mqttProp.setHost("broker.emqx.io");
      mqttProp.setPort(1883);
//        mqttProp.setUsername("");
//        mqttProp.setPassword("");
      List<MqttTopic> topics = new ArrayList<>();
      topics.add(new MqttTopic("/flink-connector/mqtt/source/test", 0));
      var source = env.fromSource(new MqttSource(mqttProp, topics), WatermarkStrategy.noWatermarks(), "Mqtt Source");
      source.map(v -> v.getTopic() + ":" + new String(v.getPayload())).print();
      env.execute("MQTT Source Test");
   }

}
```
