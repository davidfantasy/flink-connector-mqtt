package com.github.davidfantasy.flink.connector.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MqttMessage {

    private String topic;

    private Integer qos;

    private byte[] payload;

}
