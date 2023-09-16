package com.github.davidfantasy.flink.connector.mqtt;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

public class MqttTableSqlTest {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance().inStreamingMode()
                .build();
        String createTableSql = "CREATE TABLE mqttTest (\n" +
                "  id INTEGER,\n" +
                "  code STRING\n" +
                ") WITH (\n" +
                "   'connector' = 'mqtt',\n" +
                "   'server' = 'broker.emqx.io',\n" +
                "   'port' = '1883', \n" +
                "   'topic' = '/flink-connector/mqtt/source/test'\n" +
                ")";
        TableEnvironment tEnv = TableEnvironment.create(settings);
        tEnv.executeSql(createTableSql);
        TableResult tr = tEnv.executeSql("select * from mqttTest");
        tr.print();
    }

}
