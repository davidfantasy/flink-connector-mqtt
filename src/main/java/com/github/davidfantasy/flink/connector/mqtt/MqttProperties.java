package com.github.davidfantasy.flink.connector.mqtt;

import lombok.Data;

import java.io.Serializable;

@Data
public class MqttProperties implements Serializable {

    private String host;

    private Integer port;

    /**
     * 用户名
     */
    private String username = "";

    /**
     * 密码
     */
    private String password = "";

}
