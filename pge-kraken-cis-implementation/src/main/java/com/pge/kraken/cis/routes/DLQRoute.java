package com.pge.kraken.cis.routes;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.processors.ErrorProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DLQRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DLQRoute.class);

    @Override
    public void configure() {
        from("direct:dlq")
                .routeId("dlq-managed-route")
                .process(exchange -> {
                    LOG.warn("Message routed to dead letter queue");
                    StructuredLogger.warn(exchange, "DLQ_ROUTE", "Message captured in dead letter queue");
                })
                .process(new ErrorProcessor())
                .log("Message captured in dead letter queue");
    }
}
