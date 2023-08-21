package com.github.davidfantasy.flink.connector.mqtt.source;

import org.apache.flink.core.io.SimpleVersionedSerializer;

import java.io.*;

public class MqttSourceSplitSerializer implements SimpleVersionedSerializer<MqttSourceSplit> {

    private static final int VERSION = 1;

    @Override
    public int getVersion() {
        return VERSION;
    }

    @Override
    public byte[] serialize(MqttSourceSplit obj) throws IOException {
        //TODO: 暂时用java对象序列化机制进行测试
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        }
    }

    @Override
    public MqttSourceSplit deserialize(int version, byte[] serialized) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            // 创建一个ObjectInputStream
            // 读取对象
            Object obj = null;
            obj = in.readObject();
            return (MqttSourceSplit) obj;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
