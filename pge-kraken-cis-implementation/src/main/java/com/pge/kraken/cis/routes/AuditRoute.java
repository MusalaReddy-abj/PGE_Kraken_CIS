package com.pge.kraken.cis.routes;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.services.AuditService;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(AuditRoute.class);

    @Override
    public void configure() {
        from("direct:audit")
                .routeId("audit-route")
                .process(exchange -> {
                    LOG.info("Audit route invoked");
                    StructuredLogger.info(exchange, "AUDIT_ROUTE_START", "Audit processing started");
                })
                .bean(new AuditService(), "auditExchange")
                .process(exchange -> {
                    StructuredLogger.info(exchange, "AUDIT_ROUTE_COMPLETE", "Audit entry recorded");
                    LOG.info("Audit entry recorded");
                });
    }
}
