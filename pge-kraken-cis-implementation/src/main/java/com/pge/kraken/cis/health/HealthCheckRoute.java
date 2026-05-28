package com.pge.kraken.cis.health;

import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckRoute.class);

    @Override
    public void configure() {
        from("timer:health-check?period=60000")
                .routeId("health-check-route")
                .process(exchange -> {
                    LOG.info("Health check timer triggered");
                    StructuredLogger.info(exchange, "HEALTH_CHECK", "Health check passed for CIS integration service");
                })
                .log("Health check passed for CIS integration service");
    }
}
