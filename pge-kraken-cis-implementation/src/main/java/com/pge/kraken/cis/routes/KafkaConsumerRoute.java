package com.pge.kraken.cis.routes;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.processors.HeaderEnrichmentProcessor;
import com.pge.kraken.cis.processors.RequestValidationProcessor;
import com.pge.kraken.cis.processors.ResponseProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerRoute.class);
    private static final String BROKERS = "localhost:9092";
    private static final String CONSUMER_TOPIC = "cis.on-demand-read.input";
    private static final String GROUP_ID = "cis-consumer-group";

    @Override
    public void configure() {
        LOG.info("Configuring Kafka consumer route");

        fromF("kafka:%s?brokers=%s&groupId=%s&autoOffsetReset=earliest", CONSUMER_TOPIC, BROKERS, GROUP_ID)
                .routeId("kafka-consumer-route")
                .process(exchange -> {
                    LOG.info("Received Kafka message from topic {}", exchange.getIn().getHeader("kafka.TOPIC"));
                    StructuredLogger.info(exchange, "KAFKA_MESSAGE_RECEIVED", "Kafka message received for processing");
                })
                .process(new RequestValidationProcessor())
                .process(new HeaderEnrichmentProcessor())
                .to("direct:hes-on-demand")
                .process(new ResponseProcessor())
                .to("direct:audit")
                .to("log:processed-message");

        from("direct:hes-on-demand")
                .routeId("hes-on-demand-route")
                .process(exchange -> {
                    LOG.info("Calling downstream HES service");
                    StructuredLogger.info(exchange, "HES_DOWNSTREAM_CALL", "Calling downstream HES service");
                })
                .to("direct:hes-route");
    }
}
