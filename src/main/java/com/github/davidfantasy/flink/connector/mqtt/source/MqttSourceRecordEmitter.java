package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttMessage;
import org.apache.flink.api.connector.source.SourceOutput;
import org.apache.flink.connector.base.source.reader.RecordEmitter;
import org.apache.flink.table.data.RowData;

public class MqttSourceRecordEmitter implements RecordEmitter<RowData, RowData, MqttSourceSplitState> {


    @Override
    public void emitRecord(RowData element, SourceOutput<RowData> output, MqttSourceSplitState splitState) throws Exception {
        output.collect(element, System.currentTimeMillis());
    }

}
