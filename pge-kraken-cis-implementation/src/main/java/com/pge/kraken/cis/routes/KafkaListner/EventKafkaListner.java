package com.pge.kraken.cis.routes.KafkaListner;

import com.pge.kraken.cis.configs.KafkaPropertiesConfig;
import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pge.kraken.cis.processors.KongForwardingProcessor;
import com.pge.kraken.cis.processors.SoapEnvelopeProcessor;

public class EventKafkaListner extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(EventKafkaListner.class);
    private final KafkaPropertiesConfig kafkaConfig = KafkaPropertiesConfig.fromProperties();

    @Override
    public void configure() {
        LOG.info("Configuring EventKafkaListner route");

        String brokers = kafkaConfig.getBrokers();
        String topic = kafkaConfig.getDeviceEventsTopic();
        String groupId = kafkaConfig.getConsumerGroupId();

        fromF("kafka:%s?brokers=%s&groupId=%s&autoOffsetReset=earliest", topic, brokers, groupId)
                .routeId("event-kafka-listener")
                .process(exchange -> {
                    LOG.info("Received Kafka event on topic {}", topic);
                    StructuredLogger.info(exchange, "EVENT_KAFKA_RECEIVED", "Received event from Kafka topic: " + topic);
                })
                .process(new SoapEnvelopeProcessor())
                .process(new KongForwardingProcessor());
    }
}
