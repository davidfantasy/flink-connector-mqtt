package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import com.github.davidfantasy.flink.connector.mqtt.MqttTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MqttSourceEnumerator implements SplitEnumerator<MqttSourceSplit, MqttSplitsCheckpoint> {

    private SplitEnumeratorContext<MqttSourceSplit> context;

    private MqttProperties mqttProperties;

    private List<MqttTopic> topics;

    private List<MqttSourceSplit> currentSplits;

    public MqttSourceEnumerator(SplitEnumeratorContext<MqttSourceSplit> context, MqttProperties mqttProperties, List<MqttTopic> topics) {
        this.context = context;
        this.mqttProperties = mqttProperties;
        this.topics = topics;
    }

    @Override
    public void start() {
        this.currentSplits = createSplits();
    }

    @Override
    public void handleSplitRequest(int subtaskId, @Nullable String requesterHostname) {
        log.warn("接受到handleSplitRequest，subtaskId:{}，requesterHostname:{}", subtaskId, requesterHostname);
    }

    @Override
    public void addSplitsBack(List<MqttSourceSplit> splits, int subtaskId) {
        log.warn("MQTT source Enumerator退回了一些分片：{},{}", splits, subtaskId);
        //对于从失败的reader中退回的分片，重新进行分配
        currentSplits.addAll(splits);
    }

    @Override
    public void addReader(int subtaskId) {
        //TODO: 应该抽象出分配策略
        log.info("添加了一个新的reader,{},{}", subtaskId, context.registeredReaders().get(subtaskId));
        assignSplits(subtaskId);
    }

    @Override
    public MqttSplitsCheckpoint snapshotState(long checkpointId) throws Exception {
        //没啥用，当前没有需要保存的状态
        return new MqttSplitsCheckpoint();
    }

    @Override
    public void close() throws IOException {
        //没有资源需要释放
    }


    private List<MqttSourceSplit> createSplits() {
        List<MqttSourceSplit> splits = new ArrayList<>();
        int idx = 0;
        for (MqttTopic topic : topics) {
            idx++;
            MqttSourceSplit split = new MqttSourceSplit(String.valueOf(idx), topic.getTopic(), topic.getQos(), mqttProperties);
            splits.add(split);
        }
        return splits;
    }


    private void assignSplits(int readerId) {
        for (MqttSourceSplit split : currentSplits) {
            context.assignSplit(split, readerId);
        }
        currentSplits.clear();
    }

}
