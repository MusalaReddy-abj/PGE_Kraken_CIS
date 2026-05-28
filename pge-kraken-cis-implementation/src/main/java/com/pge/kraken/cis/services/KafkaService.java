package com.pge.kraken.cis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);

    public void publish(String topic, Object payload) {
        LOG.info("Publishing message to topic {}", topic);
    }
}
