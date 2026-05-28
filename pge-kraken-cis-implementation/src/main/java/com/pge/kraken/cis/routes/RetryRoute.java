package com.pge.kraken.cis.routes;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.processors.ErrorProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RetryRoute.class);

    @Override
    public void configure() {
        from("direct:retry")
                .routeId("retry-route")
                .process(exchange -> {
                    LOG.warn("Retry route invoked");
                    StructuredLogger.warn(exchange, "RETRY_ROUTE", "Retrying failed message");
                })
                .process(new ErrorProcessor())
                .log("Retrying failed message")
                .to("direct:hes-on-demand");
    }
}
