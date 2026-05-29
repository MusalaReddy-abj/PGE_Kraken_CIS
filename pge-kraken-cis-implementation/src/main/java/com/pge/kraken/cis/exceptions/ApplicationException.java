package com.pge.kraken.cis.exceptions;

import org.apache.camel.Exchange;

public class ApplicationException extends RuntimeException {

    private String traceId;
    private String trackingId;
    private String businessIdentifier;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException withContext(Exchange exchange) {
        if (exchange != null && exchange.getIn() != null) {
            this.traceId = exchange.getIn().getHeader("traceId", String.class);
            this.trackingId = exchange.getIn().getHeader("trackingId", String.class);
            this.businessIdentifier = exchange.getIn().getHeader("businessIdentifier", String.class);
        }

        return this;
    }

    public ApplicationException withContext(String traceId, String trackingId, String businessIdentifier) {
        this.traceId = traceId;
        this.trackingId = trackingId;
        this.businessIdentifier = businessIdentifier;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getBusinessIdentifier() {
        return businessIdentifier;
    }
}
