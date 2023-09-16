package com.github.davidfantasy.flink.connector.mqtt;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.data.*;
import org.apache.flink.types.RowKind;

import java.util.Collections;

import static org.apache.flink.util.Preconditions.checkNotNull;

@Data
@Slf4j
public class MqttMessage implements RowData {

    private String topic;

    private Integer qos;

    private byte[] payload;

    private final Object[] fields;

    private RowKind kind;

    public MqttMessage(String topic, Integer qos, byte[] payload, RowKind kind) {
        Object[] msgFields;
        this.topic = topic;
        this.qos = qos;
        this.payload = payload;
        if (this.payload != null) {
            try {
                msgFields = JsonConverter.deserialize(payload).values().toArray();
            } catch (Exception e) {
                log.warn("data is not a valid json:" + new String(payload));
                msgFields = Collections.emptyList().toArray();
            }
        } else {
            msgFields = Collections.emptyList().toArray();
        }
        fields = msgFields;
        checkNotNull(kind);
        this.kind = kind;
    }

    @Override
    public int getArity() {
        return fields.length;
    }

    @Override
    public RowKind getRowKind() {
        return RowKind.INSERT;
    }

    @Override
    public void setRowKind(RowKind kind) {
        checkNotNull(kind);
        this.kind = kind;
    }

    @Override
    public boolean isNullAt(int pos) {
        return this.fields[pos] == null;
    }

    @Override
    public boolean getBoolean(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Boolean.parseBoolean(val.toString());
        }
        return false;
    }

    @Override
    public byte getByte(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Byte.parseByte(val.toString());
        }
        return 0;
    }

    @Override
    public short getShort(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Short.parseShort(val.toString());
        }
        return 0;
    }

    @Override
    public int getInt(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Integer.parseInt(val.toString());
        }
        return 0;
    }

    @Override
    public long getLong(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Long.parseLong(val.toString());
        }
        return 0;
    }

    @Override
    public float getFloat(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Float.parseFloat(val.toString());
        }
        return 0;
    }

    @Override
    public double getDouble(int pos) {
        var val = this.fields[pos];
        if (val != null) {
            return Double.parseDouble(val.toString());
        }
        return 0;
    }

    @Override
    public StringData getString(int pos) {
        var o = this.fields[pos];
        if (o == null) {
            return null;
        }
        return StringData.fromString(this.fields[pos].toString());
    }

    @Override
    public DecimalData getDecimal(int pos, int precision, int scale) {
        return (DecimalData) this.fields[pos];
    }

    @Override
    public TimestampData getTimestamp(int pos, int precision) {
        return (TimestampData) this.fields[pos];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> RawValueData<T> getRawValue(int pos) {
        return (RawValueData<T>) this.fields[pos];
    }

    @Override
    public byte[] getBinary(int pos) {
        return (byte[]) this.fields[pos];
    }

    @Override
    public ArrayData getArray(int pos) {
        return (ArrayData) this.fields[pos];
    }

    @Override
    public MapData getMap(int pos) {
        return (MapData) this.fields[pos];
    }

    @Override
    public RowData getRow(int pos, int numFields) {
        return (RowData) this.fields[pos];
    }

}
