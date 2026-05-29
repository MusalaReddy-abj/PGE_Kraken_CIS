package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpExceptionProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(FtpExceptionProcessor.class);

    @Override
    public void process(Exchange exchange) {
        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (exception != null) {
            StructuredLogger.error(exchange, "FTP_FILE_PROCESSING_ERROR",
                    String.format("Failed to process FTP file: %s", fileName != null ? fileName : "unknown"), exception);
            LOG.error("Failed to process FTP file: {}", fileName != null ? fileName : "unknown", exception);
        } else {
            StructuredLogger.warn(exchange, "FTP_FILE_PROCESSING_ERROR",
                    String.format("FTP file processing failed without captured exception for file: %s", fileName != null ? fileName : "unknown"));
            LOG.warn("FTP file processing failed without captured exception for file: {}", fileName != null ? fileName : "unknown");
        }
    }
}
