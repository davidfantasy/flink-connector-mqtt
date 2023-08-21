package com.github.davidfantasy.flink.connector.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MqttTopic implements Serializable {

    private String topic;

    private Integer qos;

}
