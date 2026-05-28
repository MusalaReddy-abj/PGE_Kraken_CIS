package com.pge.kraken.cis.constants;

public final class KafkaTopics {

    public static final String INPUT_TOPIC = "cis.on-demand-read.input";
    public static final String OUTPUT_TOPIC = "cis.on-demand-read.output";
    public static final String DLQ_TOPIC = "cis.on-demand-read.dlq";

    private KafkaTopics() {
    }
}
