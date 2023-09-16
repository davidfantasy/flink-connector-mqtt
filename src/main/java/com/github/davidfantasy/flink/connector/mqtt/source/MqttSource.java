package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import com.github.davidfantasy.flink.connector.mqtt.MqttTopic;
import org.apache.flink.api.connector.source.*;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.table.data.RowData;

import java.util.List;

public class MqttSource implements Source<RowData, MqttSourceSplit, MqttSplitsCheckpoint> {

    private MqttProperties mqttProperties;

    private List<MqttTopic> topics;

    public MqttSource(MqttProperties mqttProperties, List<MqttTopic> topics) {
        if (mqttProperties == null || topics == null || topics.isEmpty()) {
            throw new IllegalStateException("MQTT配置信息缺失");
        }
        this.mqttProperties = mqttProperties;
        this.topics = topics;
    }

    @Override
    public Boundedness getBoundedness() {
        return Boundedness.CONTINUOUS_UNBOUNDED;
    }

    @Override
    public SplitEnumerator<MqttSourceSplit, MqttSplitsCheckpoint> createEnumerator(SplitEnumeratorContext<MqttSourceSplit> enumContext) throws Exception {
        return new MqttSourceEnumerator(enumContext, mqttProperties, topics);
    }

    @Override
    public SplitEnumerator<MqttSourceSplit, MqttSplitsCheckpoint> restoreEnumerator(SplitEnumeratorContext<MqttSourceSplit> enumContext, MqttSplitsCheckpoint checkpoint) throws Exception {
        return new MqttSourceEnumerator(enumContext, mqttProperties, topics);
    }

    @Override
    public SimpleVersionedSerializer<MqttSourceSplit> getSplitSerializer() {
        return new MqttSourceSplitSerializer();
    }

    @Override
    public SimpleVersionedSerializer<MqttSplitsCheckpoint> getEnumeratorCheckpointSerializer() {
        return new MqttSourceEnumeratorCheckpointSerializer();
    }

    @Override
    public SourceReader<RowData, MqttSourceSplit> createReader(SourceReaderContext readerContext) throws Exception {
        return new MqttSourceReader(readerContext);
    }

}
