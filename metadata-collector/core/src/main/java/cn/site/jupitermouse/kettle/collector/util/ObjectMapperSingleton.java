package cn.site.jupitermouse.kettle.collector.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * <p>
 * ObjectMapper 单例
 * </p>
 *
 * @author JupiterMouse 2020/11/15
 * @since 1.0
 */
public class ObjectMapperSingleton {

    private ObjectMapperSingleton() {
    }

    private static class LazyHolder {
        private static final ObjectMapper INSTANCE = build();
    }

    public static ObjectMapper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static ObjectMapper build() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
