package com.pge.kraken.cis.services;

import com.pge.kraken.cis.configs.KafkaPropertiesConfig;
import com.pge.kraken.cis.constants.KafkaTopics;
import com.pge.kraken.cis.exceptions.TechnicalException;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.models.event.D1DeviceEventSeeder;
import com.pge.kraken.cis.utils.TraceUtil;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);
    private final KafkaPropertiesConfig kafkaConfig = KafkaPropertiesConfig.fromProperties();

    /**
     * Generic method to send any message to Kafka
     *
     * @param topic   the Kafka topic
     * @param message the message payload
     * @param exchange the Camel exchange context
     */
    public void sendMessage(String topic, Object message, Exchange exchange) {
        ProducerTemplate template = exchange.getContext().createProducerTemplate();

        try {
            Map<String, Object> headers = buildKafkaHeaders(message, exchange);
            template.sendBodyAndHeaders("kafka:" + topic, message, headers);

            StructuredLogger.info(exchange, "MESSAGE_SENT_TO_KAFKA",
                    String.format("Message sent to Kafka topic: %s", topic));
        } catch (Exception e) {
            StructuredLogger.error(exchange, "KAFKA_SEND_ERROR",
                    String.format("Failed to send message to Kafka topic %s: %s", topic, e.getMessage()), e);

            String dlqTopic = kafkaConfig.getDeviceEventsDlqTopic() != null && !kafkaConfig.getDeviceEventsDlqTopic().isBlank()
                    ? kafkaConfig.getDeviceEventsDlqTopic()
                    : KafkaTopics.DLQ_TOPIC;

            try {
                Map<String, Object> headers = buildKafkaHeaders(message, exchange);
                template.sendBodyAndHeaders("kafka:" + dlqTopic, message, headers);
                StructuredLogger.warn(exchange, "KAFKA_DLQ_ROUTED",
                        String.format("Failed message routed to DLQ topic: %s", dlqTopic));

                TechnicalException routedToDlqFailure = new TechnicalException(
                        String.format("Failed to send message to Kafka topic %s; routed to DLQ topic %s", topic, dlqTopic),
                        e);
                throw routedToDlqFailure.withContext(exchange);
            } catch (Exception dlqException) {
                StructuredLogger.error(exchange, "KAFKA_DLQ_ERROR",
                        String.format("Failed to route message to DLQ topic %s: %s", dlqTopic, dlqException.getMessage()), dlqException);

                TechnicalException dlqFailure = new TechnicalException(
                        String.format("Failed to send message to Kafka topic %s and failed to route to DLQ topic %s", topic, dlqTopic),
                        e);
                dlqFailure.addSuppressed(dlqException);
                throw dlqFailure.withContext(exchange);
            }
        }
    }

    private Map<String, Object> buildKafkaHeaders(Object message, Exchange exchange) {
        Map<String, Object> headers = new HashMap<>();

        String traceId = exchange.getIn().getHeader("traceId", String.class);
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceUtil.getTraceId();
            exchange.getIn().setHeader("traceId", traceId);
        }
        putHeader(headers, "traceId", traceId);

        putHeader(headers, "fileName", exchange.getIn().getHeader("fileName", String.class));
        putHeader(headers, "batchId", exchange.getIn().getHeader("batchId", String.class));

        if (message instanceof D1DeviceEventSeeder event) {
            putHeader(headers, "deviceIdentifierNumber", event.getDeviceIdentifierNumber());
            putHeader(headers, "eventName", event.getExternalEventName());
        } else {
            putHeader(headers, "deviceIdentifierNumber", exchange.getIn().getHeader("deviceIdentifierNumber", String.class));
            putHeader(headers, "eventName", exchange.getIn().getHeader("eventName", String.class));
        }

        return headers;
    }

    private void putHeader(Map<String, Object> headers, String key, Object value) {
        if (value != null && !(value instanceof String valueString && valueString.isBlank())) {
            headers.put(key, value);
        }
    }

    /**
     * Send an event message to Kafka
     * 
     * @param topic   the Kafka topic
     * @param messageXml the XML message payload
     * @param exchange the Camel exchange context
     */
    public void publishEvent(String topic, String messageXml, Exchange exchange) {
        sendMessage(topic, messageXml, exchange);
    }

    /**
     * Legacy method for backward compatibility
     * 
     * @param topic the Kafka topic
     * @param payload the message payload
     */
    public void publish(String topic, Object payload) {
        LOG.info("Publishing message to topic {}", topic);
    }
}
