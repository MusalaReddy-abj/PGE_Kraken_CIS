package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.configs.KafkaPropertiesConfig;
import com.pge.kraken.cis.exceptions.TechnicalException;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.mapper.EventMessageMapper;
import com.pge.kraken.cis.models.event.D1DeviceEventSeeder;
import com.pge.kraken.cis.services.KafkaService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class EventXmlProcessor implements Processor {

    private final KafkaService kafkaService = new KafkaService();
    private final KafkaPropertiesConfig kafkaConfig = KafkaPropertiesConfig.fromProperties();

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);

        try {
            String xmlContent = exchange.getIn().getBody(String.class);

            StructuredLogger.info(exchange, "XML_PARSING_STARTED",
                    String.format("Starting to parse XML file: %s", fileName));

            List<D1DeviceEventSeeder> events = processEventNodes(xmlContent, exchange);

            StructuredLogger.info(exchange, "XML_PARSING_COMPLETED",
                    String.format("Parsed %d events from XML file: %s", events.size(), fileName));

            if (events.isEmpty()) {
                StructuredLogger.warn(exchange, "NO_EVENTS_EXTRACTED",
                        String.format("No valid events extracted from file: %s", fileName));
            }

            exchange.getMessage().setBody(events);
            exchange.getMessage().setHeader("EventCount", events.size());
            exchange.getMessage().setHeader("SourceFileName", fileName);

            StructuredLogger.info(exchange, "XML_FILE_PROCESSED",
                    String.format("XML file processed successfully with %d events from file: %s", events.size(), fileName));

        } catch (Exception e) {
            StructuredLogger.error(exchange, "XML_PROCESSING_ERROR",
                    String.format("Error processing XML file: %s", e.getMessage()), e);
            throw new TechnicalException(String.format("Failed to process XML file: %s", fileName), e)
                    .withContext(exchange);
        }
    }

    private List<D1DeviceEventSeeder> processEventNodes(String xmlContent, Exchange exchange) throws Exception {
        List<D1DeviceEventSeeder> events = new ArrayList<>();

        NodeList eventNodes = EventMessageMapper.extractEventNodes(xmlContent);
       
        StructuredLogger.info(exchange, "XML_EVENT_NODES_FOUND",
                String.format("Found %d XML event nodes to process", eventNodes.getLength()));

        for (int i = 0; i < eventNodes.getLength(); i++) {
            D1DeviceEventSeeder event = null;
            try {
                Element eventElement = (Element) eventNodes.item(i);
                event = EventMessageMapper.mapEventElement(eventElement);

                if (event != null) {
                    String deviceIdentifierNumber = event.getDeviceIdentifierNumber();

                    StructuredLogger.info(exchange, "XML_EVENT_PROCESSING",
                            String.format("Processing extracted XML event to Kafka at index %d for deviceIdentifierNumber %s", i, deviceIdentifierNumber));
                    sendEventToKafka(event, exchange);
                    events.add(event);
                }
            } catch (Exception e) {
                String deviceIdentifierNumber = event != null ? event.getDeviceIdentifierNumber() : null;

                if (deviceIdentifierNumber != null) {
                    StructuredLogger.error(exchange, "XML_EVENT_PROCESSING_ERROR",
                            String.format("Failed to process XML event at index %d for deviceIdentifierNumber %s", i, deviceIdentifierNumber), e);
                } else {
                    StructuredLogger.error(exchange, "XML_EVENT_PROCESSING_ERROR",
                            String.format("Failed to process XML event at index %d", i), e);
                }
            }
        }

        return events;
    }

    private void sendEventToKafka(D1DeviceEventSeeder event, Exchange exchange) {
        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
        exchange.getIn().setHeader("fileName", fileName);
        exchange.getIn().setHeader("eventName", event.getExternalEventName());
        exchange.getIn().setHeader("deviceIdentifierNumber", event.getDeviceIdentifierNumber());
        exchange.getIn().setHeader("eventsDLQtopic", kafkaConfig.getDeviceEventsDlqTopic());

        String eventXml = event.toString();
        kafkaService.sendMessage(kafkaConfig.getDeviceEventsTopic(), eventXml, exchange);
    }
}
