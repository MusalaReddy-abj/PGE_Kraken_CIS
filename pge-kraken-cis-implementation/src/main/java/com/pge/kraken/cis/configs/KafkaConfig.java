package com.pge.kraken.cis.configs;

import lombok.Data;

@Data
public class KafkaConfig {

    private String brokers;
    private String consumerTopic;
    private String producerTopic;
    private String groupId;
}
