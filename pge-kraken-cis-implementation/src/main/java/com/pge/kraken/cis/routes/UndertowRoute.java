package com.pge.kraken.cis.routes;

import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndertowRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(UndertowRoute.class);

    @Override
    public void configure() {
        from("undertow:http://0.0.0.0:8080?matchOnUriPrefix=true")
                .routeId("undertow-http-listener")
                .choice()
                    .when(header("CamelHttpPath").isEqualTo("/health"))
                        .process(exchange -> {
                            LOG.info("Health endpoint invoked");
                            StructuredLogger.info(exchange, "HEALTH_ENDPOINT_REQUEST", "Health endpoint request received");
                        })
                        .setBody(constant("Apache Camel + Undertow is running"))
                        .setHeader("Content-Type", constant("text/plain"))
                    .when(header("CamelHttpPath").isEqualTo("/v1/ondemandread"))
                        .to("direct:hes-http-request")
                    .otherwise()
                        .setHeader("Content-Type", constant("text/plain"))
                        .setBody(constant("Not Found"))
                        .setHeader("CamelHttpResponseCode", constant(404))
                .end();
    }
}
