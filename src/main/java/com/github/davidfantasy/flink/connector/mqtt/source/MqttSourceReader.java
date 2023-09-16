package com.github.davidfantasy.flink.connector.mqtt.source;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.connector.base.source.reader.SingleThreadMultiplexSourceReaderBase;
import org.apache.flink.table.data.RowData;

import java.util.Map;

@Slf4j
public class MqttSourceReader<T> extends SingleThreadMultiplexSourceReaderBase<RowData, RowData, MqttSourceSplit, MqttSourceSplitState> {

    public MqttSourceReader(SourceReaderContext context) {
        super(MqttSourceSplitReader::new, new MqttSourceRecordEmitter(), context.getConfiguration(), context);
    }

    @Override
    protected void onSplitFinished(Map<String, MqttSourceSplitState> finishedSplitIds) {
        log.info("MqttSourceReader onSplitFinished is called!!");
        //没啥需要做的
    }

    @Override
    protected MqttSourceSplitState initializedState(MqttSourceSplit split) {
        return new MqttSourceSplitState(split.getId(), split.getTopic(), split.getQos(), split.getMqttProperties());
    }

    @Override
    protected MqttSourceSplit toSplitType(String splitId, MqttSourceSplitState splitState) {
        return new MqttSourceSplit(splitState.getSplitId(), splitState.getTopic(), splitState.getQos(), splitState.getMqttProperties());
    }

}


