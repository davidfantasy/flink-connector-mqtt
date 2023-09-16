package com.github.davidfantasy.flink.connector.mqtt.table;

import com.github.davidfantasy.flink.connector.mqtt.MqttProperties;
import com.github.davidfantasy.flink.connector.mqtt.MqttTopic;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;

import java.util.HashSet;
import java.util.Set;

import static org.apache.flink.configuration.ConfigOptions.key;

public class MqttDynamicTableFactory implements DynamicTableSourceFactory {

    private static final String IDENTIFIER = "mqtt";

    private static final ConfigOption<String> SERVER =
            key("server").stringType().noDefaultValue().withDescription("mqtt server");

    private static final ConfigOption<Integer> PORT =
            key("port").intType().noDefaultValue().withDescription("mqtt port");

    private static final ConfigOption<String> USERNAME =
            key("username").stringType().defaultValue("").withDescription("mqtt server username");

    private static final ConfigOption<String> PASSWORD =
            key("password").stringType().defaultValue("").withDescription("mqtt server password");
    private static final ConfigOption<String> TOPIC =
            key("topic").stringType().noDefaultValue().withDescription("mqtt topic");

    private static final ConfigOption<Integer> QOS =
            key("qos").intType().defaultValue(0).withDescription("mqtt message qos,default 0");

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        final FactoryUtil.TableFactoryHelper helper =
                FactoryUtil.createTableFactoryHelper(this, context);
        helper.validate();
        final ReadableConfig config = helper.getOptions();
        MqttProperties properties = new MqttProperties();
        properties.setHost(config.get(SERVER));
        properties.setPort(config.get(PORT));
        properties.setUsername(config.get(USERNAME));
        properties.setPassword(config.get(PASSWORD));
        MqttTopic topic = new MqttTopic(config.get(TOPIC), config.get(QOS));
        return new MqttDynamicSource(properties, topic);
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        Set<ConfigOption<?>> options = new HashSet<>();
        options.add(SERVER);
        options.add(PORT);
        options.add(TOPIC);
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        Set<ConfigOption<?>> options = new HashSet<>();
        options.add(USERNAME);
        options.add(PASSWORD);
        options.add(QOS);
        return options;
    }

}
