package com.github.davidfantasy.flink.connector.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.type.TypeReference;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class JsonConverter {

    private static final ObjectMapper objectMapper = createObjectMapper(JsonInclude.Include.ALWAYS);

    public static Map<String, Object> deserialize(byte[] json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("json convert err:", e);
            return Collections.emptyMap();
        }
    }


    public static ObjectMapper createObjectMapper(JsonInclude.Include include) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(include);
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //禁止使用int代表Enum的order()来反序列化Enum,非常危险
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        // 禁用时间戳风格的日期时间序列化，该设置对 java.util.Date（及其他旧的日期时间类）和 Java8time 都有影响
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 设置时区（使用操作系统的设置），该设置仅对 java.util.Date（及其他旧的日期时间类）有影响
        mapper.setTimeZone(TimeZone.getDefault());
        // 设置格式化风格，该方法内部会禁用 WRITE_DATES_AS_TIMESTAMPS，从而使此“格式”仅对 java.util.Date（及其他旧的日期时间类）有影响
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return mapper;
    }


}
