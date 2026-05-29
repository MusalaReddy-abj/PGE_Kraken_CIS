package com.pge.kraken.cis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pge.kraken.cis.exceptions.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtil() {
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize value to JSON", e);
            throw new TechnicalException("Unable to serialize value", e);
        }
    }
}
