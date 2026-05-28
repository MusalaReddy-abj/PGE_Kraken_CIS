package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.models.response.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ErrorProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorProcessor.class);

    @Override
    public void process(Exchange exchange) {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (exception != null) {
            StructuredLogger.error(exchange, "PROCESSING_ERROR", "Processing error handled", exception);
            LOG.error("Processing error handled", exception);
        } else {
            StructuredLogger.warn(exchange, "PROCESSING_ERROR", "Unknown processing error handled");
            LOG.warn("Unknown processing error handled");
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("CIS-ERR-001");
        errorResponse.setErrorMessage(exception != null ? exception.getMessage() : "Unknown processing error");
        errorResponse.setTimestamp(LocalDateTime.now());
        exchange.getMessage().setBody(errorResponse);
    }
}
