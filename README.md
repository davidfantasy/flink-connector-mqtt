## flink-connector-mqtt

基于flink最新的[FLIP-27](https://cwiki.apache.org/confluence/display/FLINK/FLIP-27%3A+Refactor+Source+Interface)架构对MQTT
connector的实现，主要特性如下：

- 兼容最新的flink版本（1.17.1）
- 支持多个topic同时读取数据，并基于topic进行自动分片
- 使用了高性能的MQTT客户端（hivemq-mqtt）
- 支持以flink sql的方式查询

## 依赖说明

- 必须使用JDK 17及以上版本
- 目前暂时只支持MQTT 3协议，后续会支持MQTT 5
- flink版本支持1.17.1及以上版本

## 使用方法
1. 引入依赖
```xml
<dependency>
   <groupId>com.github.davidfantasy</groupId>
   <artifactId>flink-connector-mqtt</artifactId>
   <version>1.1.0</version>
</dependency>
```

2. 示例代码：
作为流式数据源使用：
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
在flink sql中创建表：
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
**注意**：使用flink sql时，mqtt的消息格式必须为JSON格式，上述表结构对应的json格式为：
```json
{"id":123,"code":"some hello"}
```
目前在table中可以使用的配置为：
- connector: 固定为mqtt
- server: mqtt broker host，必须
- port: mqtt broker port，必须
- username: 认证用户名，可选
- password: 认证密码，可选
- topic: 该表对应的MQTT topic，必须
- qos: 使用什么质量等级进行订阅，可选，默认0