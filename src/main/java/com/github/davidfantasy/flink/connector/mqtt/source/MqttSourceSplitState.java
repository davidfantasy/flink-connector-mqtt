package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MqttSourceSplitState {

    private String splitId;

    private String topic;

    private Integer qos;

    private MqttProperties mqttProperties;

}
