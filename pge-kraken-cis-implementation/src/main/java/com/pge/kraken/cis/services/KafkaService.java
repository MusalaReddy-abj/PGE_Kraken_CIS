package com.pge.kraken.cis.services;

import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);

    /**
     * Generic method to send any message to Kafka
     *
     * @param topic   the Kafka topic
     * @param message the message payload
     * @param exchange the Camel exchange context
     */
    public void sendMessage(String topic, Object message, Exchange exchange) {
        try {
            ProducerTemplate template = exchange.getContext().createProducerTemplate();
            template.sendBody("kafka:" + topic, message);

            StructuredLogger.info(exchange, "MESSAGE_SENT_TO_KAFKA",
                    String.format("Message sent to Kafka topic: %s", topic));

            LOG.info("Message sent to Kafka topic: {}", topic);
        } catch (Exception e) {
            StructuredLogger.error(exchange, "KAFKA_SEND_ERROR",
                    String.format("Failed to send message to Kafka topic %s: %s", topic, e.getMessage()));
            LOG.error("Error sending message to Kafka topic {}", topic, e);
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
