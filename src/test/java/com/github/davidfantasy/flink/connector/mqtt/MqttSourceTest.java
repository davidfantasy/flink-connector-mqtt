package com.github.davidfantasy.flink.connector.mqtt;

import com.github.davidfantasy.flink.connector.mqtt.source.MqttSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

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
            return msg.getTopic() + ":" + msg.getString(0);
        }).print();
        env.execute("MQTT Source Test");
    }

}
