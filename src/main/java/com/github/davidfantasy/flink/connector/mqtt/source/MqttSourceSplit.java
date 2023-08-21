package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import lombok.Data;
import org.apache.flink.api.connector.source.SourceSplit;

import java.io.Serializable;

@Data
public class MqttSourceSplit implements SourceSplit, Serializable {

    private final String id;

    private MqttProperties mqttProperties;

    /**
     * 该分片对应的mqtt topic
     */
    private final String topic;

    /**
     * 使用哪个级别的QOS定义topic
     */
    private Integer qos = 0;

    public MqttSourceSplit(String id, String topic, Integer qos, MqttProperties mqttProperties) {
        this.id = id;
        this.topic = topic;
        this.qos = qos;
        this.mqttProperties = mqttProperties;
    }

    @Override
    public String splitId() {
        return id;
    }

}
