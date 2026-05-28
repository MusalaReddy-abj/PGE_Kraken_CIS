package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.models.response.OnDemandReadResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ResponseProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseProcessor.class);

    @Override
    public void process(Exchange exchange) {
        OnDemandReadResponse response = new OnDemandReadResponse();
        response.setRequestId(String.valueOf(exchange.getIn().getHeader("correlationId")));
        response.setStatus("PROCESSED");
        response.setMessage("Request processed successfully");
        response.setProcessedAt(LocalDateTime.now());

        exchange.getMessage().setBody(response);
        StructuredLogger.info(exchange, "RESPONSE_PREPARED", "Response processed successfully");
        LOG.info("Response prepared for exchange");
    }
}
