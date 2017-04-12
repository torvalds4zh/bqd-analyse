package com.haizhi.bqd.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by chenbo on 17/4/6.
 */
public class JsonUtil {
    private static Log log = LogFactory.getLog(JsonUtil.class);
    private static ObjectMapper objectMapper = ObjectMapperFactory.get();

    public static String formatJSON(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("序列化json错误", e);
            return "";
        }
    }

    public static <T> T unformatJSON(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            log.error("反序列化json错误", e);
            return null;
        }
    }

    public static <T> T unformatJSON(Map<String, Object> json, Class<T> type) {
        return objectMapper.convertValue(json, type);
    }

    public static Object unformatJSON(String json, Class type, Class type2) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(type, type2));
        } catch (IOException e) {
            log.error("反序列化json错误", e);
            return null;
        }
    }

    /**
     * 泛型反序列化接口
     *
     * @param json
     * @param type     泛型的Collection Type
     * @param type2    elementClasses 元素类
     * @return
     */
    public static Object unformatJSON(String json, Class type, Class... type2) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(type, type2));
        } catch (IOException e) {
            log.error("反序列化json错误", e);
            return null;
        }
    }

}
