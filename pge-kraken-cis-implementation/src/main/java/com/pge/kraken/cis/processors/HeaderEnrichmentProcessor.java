package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.constants.HeaderConstants;
import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public class HeaderEnrichmentProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderEnrichmentProcessor.class);

    @Override
    public void process(Exchange exchange) {
        String correlationId = UUID.randomUUID().toString();
        exchange.getIn().setHeader(HeaderConstants.CORRELATION_ID, correlationId);
        exchange.getIn().setHeader(HeaderConstants.SOURCE_SYSTEM, "CIS");
        exchange.getIn().setHeader(HeaderConstants.PROCESSED_AT, Instant.now().toString());

        StructuredLogger.info(exchange, "HEADERS_ENRICHED", "Exchange headers enriched");
        LOG.info("Enriched headers with correlationId {}", correlationId);
    }
}
