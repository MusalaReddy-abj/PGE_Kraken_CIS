package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.exceptions.TechnicalException;
import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.pge.kraken.cis.clients.KongApiClient;
import com.pge.kraken.cis.configs.KongConfig;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KongForwardingProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(KongForwardingProcessor.class);
    private final KongConfig kongConfig = new KongConfig();
    private final KongApiClient kongClient = new KongApiClient(kongConfig);

    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getIn().getBody(String.class);
        try {
                LOG.info("Forwarding message to Kong via KongApiClient");
                String resourcePath = exchange.getIn().getHeader("kong.resource.path", String.class);
                if (resourcePath == null || resourcePath.isBlank()) {
                    resourcePath = kongConfig.getEventsResource();
                }
                String contentType = exchange.getIn().getHeader("kong.content.type", String.class);
                if (contentType == null || contentType.isBlank()) {
                    contentType = kongConfig.getContentTypeXml();
                }
                var response = kongClient.postDeviceEvent(resourcePath, payload, contentType);
                StructuredLogger.info(exchange, "KONG_FORWARD_SUCCESS",
                    String.format("Forwarded message to Kong endpoint, status=%d", response.statusCode()));
                exchange.getIn().setHeader("KongResponse", response.body());
        } catch (Exception e) {
            StructuredLogger.error(exchange, "KONG_FORWARD_ERROR",
                    String.format("Failed to forward message to Kong: %s", e.getMessage()), e);
            throw new TechnicalException(String.format("Failed to forward message to Kong"), e).withContext(exchange);
        }
    }
}
