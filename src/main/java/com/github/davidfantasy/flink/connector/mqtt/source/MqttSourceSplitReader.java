package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.DebounceTask;
import com.github.davidfantasy.flink.connector.mqtt.MqttMessage;
import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientConfig;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuth;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsAddition;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsChange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 负责从分片中获取mqtt配置信息并初始化客户端进行读取
 */
@Slf4j
public class MqttSourceSplitReader implements SplitReader<MqttMessage, MqttSourceSplit> {

    private final List<MqttSourceSplit> splits;
    private final String MQTT_CLIENTID_PREFIX = "flink-mqtt-connector:";
    private final long MSG_CACHE_LIMIT = 100000;
    private BlockingQueue<MqttMessage> messageQueue;
    private final DebounceTask warningPrinter;
    private List<Mqtt3AsyncClient> mqttClients;

    public MqttSourceSplitReader() {
        this.splits = new ArrayList<>();
        messageQueue = new LinkedBlockingQueue<>(10000);
        warningPrinter = DebounceTask.build(() -> {
            log.warn("消息缓存队列已满，数据将被丢弃");
        }, 5000L);
    }

    @Override
    public RecordsWithSplitIds<MqttMessage> fetch() throws IOException {
        if (this.splits.size() == 0) {
            return new MqttRecords(null, null);
        }
        initMqttClient();
        String spilitId = this.splits.get(0).getId();
        try {
            List<MqttMessage> datas = null;
            var firstMsg = messageQueue.take();
            if (messageQueue.isEmpty()) {
                datas = Collections.singletonList(firstMsg);
            } else {
                datas = new ArrayList<>(messageQueue.size() + 1);
                datas.add(firstMsg);
                datas.addAll(messageQueue);
            }
            messageQueue.clear();
            return new MqttRecords(spilitId, datas.iterator());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleSplitsChanges(SplitsChange<MqttSourceSplit> splitsChanges) {
        if (!(splitsChanges instanceof SplitsAddition)) {
            throw new UnsupportedOperationException(
                    String.format(
                            "The SplitChange type of %s is not supported.",
                            splitsChanges.getClass()));
        }
        splits.addAll(splitsChanges.splits());
    }

    @Override
    public void wakeUp() {

    }

    @Override
    public void close() throws Exception {
        mqttClients.forEach(c -> {
            try {
                c.disconnect().get(3000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("关闭客户端连接时发生错误：{}", e.getMessage());
            }
        });
    }

    private void initMqttClient() {
        if (mqttClients != null) {
            return;
        }
        mqttClients = new ArrayList<>();
        //为每个分片初始化一个client实例
        try {
            for (MqttSourceSplit split : splits) {
                var client = buildMqttClient(split);
                client.connect().get(5000, TimeUnit.MILLISECONDS);
                client.subscribeWith()
                        .topicFilter(split.getTopic())
                        .qos(convertMqttQos(split.getQos()))
                        .callback(this::handleReceivedMsg).send();
                mqttClients.add(client);
            }
        } catch (Exception e) {
            throw new IllegalStateException("初始化mqtt 客户端发生错误", e);
        }
    }

    private Mqtt3AsyncClient buildMqttClient(MqttSourceSplit split) {
        MqttProperties mqttProp = split.getMqttProperties();
        Mqtt3ClientBuilder builder = MqttClient.builder().useMqttVersion3()
                .serverHost(mqttProp.getHost())
                .serverPort(mqttProp.getPort())
                .simpleAuth(Mqtt3SimpleAuth.builder()
                        .username(mqttProp.getUsername())
                        .password(mqttProp.getPassword().getBytes(StandardCharsets.UTF_8))
                        .build())
                .automaticReconnectWithDefaultConfig()
                .identifier(MQTT_CLIENTID_PREFIX + split.getId())
                .addConnectedListener(context -> {
                    MqttClientConfig config = context.getClientConfig();
                    String clientId = config.getClientIdentifier().isPresent() ? config.getClientIdentifier().get().toString() : "";
                    log.info("mqtt客户端{}连接成功，连接地址：{}，端口：{}", clientId, config.getServerHost(), config.getServerPort());
                })
                .addDisconnectedListener(context -> {
                    MqttClientConfig config = context.getClientConfig();
                    String clientId = config.getClientIdentifier().isPresent() ? config.getClientIdentifier().get().toString() : "";
                    log.warn("mqtt客户端{}连接丢失 - {}", clientId, context.getCause().getMessage(), context.getCause());
                });
        return builder.buildAsync();
    }

    private MqttQos convertMqttQos(Integer qos) {
        if (qos == null) {
            return MqttQos.AT_MOST_ONCE;
        }
        return MqttQos.fromCode(qos);
    }

    private void handleReceivedMsg(Mqtt3Publish msg) {
        var mqttMsg = new MqttMessage(msg.getTopic().toString(),
                msg.getQos().getCode(),
                msg.getPayloadAsBytes());
        if (messageQueue.size() > MSG_CACHE_LIMIT) {
            warningPrinter.doTask();
            return;
        }
        messageQueue.add(mqttMsg);
    }

}
