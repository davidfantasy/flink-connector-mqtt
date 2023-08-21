package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttMessage;
import org.apache.flink.api.connector.source.SourceOutput;
import org.apache.flink.connector.base.source.reader.RecordEmitter;

public class MqttSourceRecordEmitter implements RecordEmitter<MqttMessage, MqttMessage, MqttSourceSplitState> {


    @Override
    public void emitRecord(MqttMessage element, SourceOutput<MqttMessage> output, MqttSourceSplitState splitState) throws Exception {
        output.collect(element, System.currentTimeMillis());
    }

}
