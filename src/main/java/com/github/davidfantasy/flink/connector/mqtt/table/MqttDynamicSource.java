package com.github.davidfantasy.flink.connector.mqtt.table;

import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import com.github.davidfantasy.flink.connector.mqtt.MqttTopic;
import com.github.davidfantasy.flink.connector.mqtt.source.MqttSource;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceProvider;

import java.util.List;

public class MqttDynamicSource implements ScanTableSource {

    private final MqttProperties mqttProperties;

    private final MqttTopic topic;

    public MqttDynamicSource(MqttProperties mqttProperties, MqttTopic topic) {
        this.mqttProperties = mqttProperties;
        this.topic = topic;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        return ChangelogMode.insertOnly();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {
        return SourceProvider.of(new MqttSource(mqttProperties, List.of(topic)));
    }

    @Override
    public DynamicTableSource copy() {
        return new MqttDynamicSource(mqttProperties, topic);
    }

    @Override
    public String asSummaryString() {
        return "MqttSource";
    }

}
