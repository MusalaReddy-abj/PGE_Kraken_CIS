package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.constants.KafkaTopics;
import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMessageProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageProcessor.class);

    @Override
    public void process(Exchange exchange) {
        exchange.getIn().setHeader("kafka.topic", KafkaTopics.INPUT_TOPIC);
        exchange.getIn().setHeader("kafka.consumer.group", "cis-consumer-group");

        StructuredLogger.info(exchange, "KAFKA_MESSAGE_PROCESSOR", "Kafka message metadata enriched");
        LOG.info("Kafka message metadata enriched");
    }
}
