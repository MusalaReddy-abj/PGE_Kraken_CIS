package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.configs.KafkaPropertiesConfig;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.mapper.EventMessageMapper;
import com.pge.kraken.cis.models.event.D1DeviceEventSeeder;
import com.pge.kraken.cis.services.KafkaService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class EventXmlProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EventXmlProcessor.class);
    private final KafkaService kafkaService = new KafkaService();
    private final KafkaPropertiesConfig kafkaConfig = KafkaPropertiesConfig.fromProperties();

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String xmlContent = exchange.getIn().getBody(String.class);
            String fileName = exchange.getIn().getHeader("CamelFileName", String.class);

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

            LOG.info("XML file processed successfully with {} events", events.size());

        } catch (Exception e) {
            StructuredLogger.error(exchange, "XML_PROCESSING_ERROR",
                    String.format("Error processing XML file: %s", e.getMessage()));
            LOG.error("Error processing XML file in EventXmlProcessor", e);
            throw e;
        }
    }

    private List<D1DeviceEventSeeder> processEventNodes(String xmlContent, Exchange exchange) throws Exception {
        List<D1DeviceEventSeeder> events = new ArrayList<>();

        NodeList eventNodes = EventMessageMapper.extractEventNodes(xmlContent);

        for (int i = 0; i < eventNodes.getLength(); i++) {
            Element eventElement = (Element) eventNodes.item(i);
            D1DeviceEventSeeder event = EventMessageMapper.mapEventElement(eventElement);

            if (event != null) {
                sendEventToKafka(event, exchange);
                events.add(event);
            }
        }

        return events;
    }

    private void sendEventToKafka(D1DeviceEventSeeder event, Exchange exchange) {
        String eventXml = event.toString();
        kafkaService.sendMessage(kafkaConfig.getDeviceEventsTopic(), eventXml, exchange);
    }
}
