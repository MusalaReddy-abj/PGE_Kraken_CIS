
package com.pge.kraken.cis.logging;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StructuredLogger {

    private StructuredLogger() {
    }

    private static final Logger LOG =
            LoggerFactory.getLogger(StructuredLogger.class);

    public static void info(
            Exchange exchange,
            String status,
            String message) {

        LOG.info(
                buildLogMessage(exchange, status, message)
        );
    }

    public static void info(
            String status,
            String message) {

        LOG.info(
                buildLogMessage(status, message)
        );
    }

    public static void warn(
            Exchange exchange,
            String status,
            String message) {

        LOG.warn(
                buildLogMessage(exchange, status, message)
        );
    }

    public static void warn(
            String status,
            String message) {

        LOG.warn(
                buildLogMessage(status, message)
        );
    }

    public static void error(
            Exchange exchange,
            String status,
            String message,
            Exception exception) {

        LOG.error(
                buildLogMessage(exchange, status, message)
                        + " error=" + exception.getMessage(),
                exception
        );
    }

    public static void error(
            Exchange exchange,
            String status,
            String message) {

        LOG.error(
                buildLogMessage(exchange, status, message)
        );
    }

    public static void error(
            String status,
            String message,
            Exception exception) {

        LOG.error(
                buildLogMessage(status, message)
                        + " error=" + exception.getMessage(),
                exception
        );
    }

    public static void error(
            String status,
            String message) {

        LOG.error(
                buildLogMessage(status, message)
        );
    }

    private static String buildLogMessage(
            String status,
            String message) {
        return String.format(
                "status=%s message=%s",
                safeValue(status),
                safeValue(message)
        );
    }

    private static String buildLogMessage(
            Exchange exchange,
            String status,
            String message) {

        String traceId =
                safeValue(exchange.getIn().getHeader("traceId", String.class));

        String trackingId =
                safeValue(exchange.getIn().getHeader("trackingId", String.class));

        String correlationId =
                safeValue(exchange.getIn().getHeader("correlationId", String.class));

        String businessIdentifier =
                safeValue(exchange.getIn().getHeader("businessIdentifier", String.class));

        String routeId =
                safeValue(exchange.getFromRouteId());

        Integer retryCount =
                exchange.getProperty(
                        Exchange.REDELIVERY_COUNTER,
                        Integer.class);

        if (retryCount == null) {
            retryCount = 0;
        }

        String payload =
                safeValue(exchange.getIn().getBody(String.class));

        if (payload.length() > 5000) {
            payload = payload.substring(0, 5000) + "...TRUNCATED";
        }

        return String.format(
                "status=%s traceId=%s trackingId=%s correlationId=%s businessIdentifier=%s routeId=%s retryCount=%s message=%s payload=%s",
                safeValue(status),
                traceId,
                trackingId,
                correlationId,
                businessIdentifier,
                routeId,
                retryCount,
                safeValue(message),
                payload
        );
    }

    private static String safeValue(String value) {
        return value == null ? "" : value;
    }
}
