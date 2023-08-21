package com.github.davidfantasy.flink.connector.mqtt.source;

import org.apache.flink.core.io.SimpleVersionedSerializer;

import java.io.IOException;

/**
 * 暂时没用
 */
public class MqttSourceEnumeratorCheckpointSerializer implements SimpleVersionedSerializer<MqttSplitsCheckpoint> {
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public byte[] serialize(MqttSplitsCheckpoint obj) throws IOException {
        return new byte[0];
    }

    @Override
    public MqttSplitsCheckpoint deserialize(int version, byte[] serialized) throws IOException {
        return new MqttSplitsCheckpoint();
    }
}
