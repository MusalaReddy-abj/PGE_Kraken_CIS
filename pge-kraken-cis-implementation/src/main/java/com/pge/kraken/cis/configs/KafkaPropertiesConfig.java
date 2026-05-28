package com.pge.kraken.cis.configs;

import lombok.Data;

import java.io.InputStream;
import java.util.Properties;

@Data
public class KafkaPropertiesConfig {

    private String brokers;
    private String consumerGroupId;
    private String deviceEventsTopic;

    public static KafkaPropertiesConfig fromProperties() {
        Properties props = new Properties();
        try (InputStream in = KafkaPropertiesConfig.class.getClassLoader().getResourceAsStream("kafka.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            // swallow - defaults will be used
        }

        KafkaPropertiesConfig cfg = new KafkaPropertiesConfig();
        cfg.brokers = props.getProperty("kafka.bootstrap.servers", "localhost:9092");
        cfg.consumerGroupId = props.getProperty("kafka.consumer.group.id", "cis-consumer-group");
        cfg.deviceEventsTopic = props.getProperty("kafka.device.events.topic", "cis.device-events");
        return cfg;
    }
}
