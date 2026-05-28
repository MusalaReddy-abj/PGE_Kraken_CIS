package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.exceptions.ValidationException;
import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestValidationProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(RequestValidationProcessor.class);

    @Override
    public void process(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        if (body == null) {
            StructuredLogger.warn(exchange, "VALIDATION_FAILED", "Request body is required");
            LOG.warn("Request validation failed: request body is required");
            throw new ValidationException("Request body is required");
        }

        String payload = body.toString().trim();
        if (payload.isEmpty()) {
            StructuredLogger.warn(exchange, "VALIDATION_FAILED", "Request payload cannot be empty");
            LOG.warn("Request validation failed: request payload cannot be empty");
            throw new ValidationException("Request payload cannot be empty");
        }

        exchange.getIn().setHeader("validated", true);
        StructuredLogger.info(exchange, "VALIDATION_PASSED", "Request validation passed");
        LOG.info("Request validation passed");
    }
}
