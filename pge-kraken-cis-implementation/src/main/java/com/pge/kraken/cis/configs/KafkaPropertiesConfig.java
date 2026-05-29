package com.pge.kraken.cis.configs;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

@Data
public class KafkaPropertiesConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaPropertiesConfig.class);

    private String brokers;
    private String consumerGroupId;
    private String deviceEventsTopic;
    private String deviceEventsDlqTopic;
    private String rcdcHesResponseTopic;

    public static KafkaPropertiesConfig fromProperties() {
        Properties props = new Properties();
        try (InputStream in = KafkaPropertiesConfig.class.getClassLoader().getResourceAsStream("kafka.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            LOG.warn("Failed to load Kafka configuration from kafka.properties; using defaults", e);
        }

        KafkaPropertiesConfig cfg = new KafkaPropertiesConfig();
        cfg.brokers = props.getProperty("kafka.bootstrap.servers", "localhost:9092");
        cfg.consumerGroupId = props.getProperty("kafka.consumer.group.id", "cis-consumer-group");
        cfg.deviceEventsTopic = props.getProperty("kafka.device.events.topic", "cis.device-events");
        cfg.deviceEventsDlqTopic = props.getProperty("kafka.device.events.dlq.topic", "cis.device-events.dlq");
        cfg.rcdcHesResponseTopic = props.getProperty(
                "kafka.rcdc.hes.response.topic",
                "pge.hes.connect.disconnect.response");
        return cfg;
    }
}
