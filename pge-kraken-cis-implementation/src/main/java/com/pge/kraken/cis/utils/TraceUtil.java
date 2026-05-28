package com.pge.kraken.cis.utils;

import org.apache.camel.Exchange;

import java.util.UUID;

public final class TraceUtil {

    private TraceUtil() {
    }

    public static String getTraceId() {
        return UUID.randomUUID().toString();
    }

    public static CorrelationIds populateCorrelationHeaders(Exchange exchange) {
        String traceId = exchange.getIn().getHeader("traceId", String.class);
        if (traceId == null || traceId.isBlank()) {
            traceId = getTraceId();
            exchange.getIn().setHeader("traceId", traceId);
        }

        String trackingId = exchange.getIn().getHeader("trackingId", String.class);
        if (trackingId == null || trackingId.isBlank()) {
            trackingId = getTraceId();
            exchange.getIn().setHeader("trackingId", trackingId);
        }

        return new CorrelationIds(traceId, trackingId);
    }

    public record CorrelationIds(String traceId, String trackingId) {
    }
}
