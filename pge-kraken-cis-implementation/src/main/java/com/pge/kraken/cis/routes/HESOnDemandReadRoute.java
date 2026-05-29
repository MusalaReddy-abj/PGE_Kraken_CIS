package com.pge.kraken.cis.routes;

import com.pge.kraken.cis.exceptions.ValidationException;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.services.HESService;
import com.pge.kraken.cis.utils.TraceUtil;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HESOnDemandReadRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HESOnDemandReadRoute.class);

    @Override
    public void configure() {
    
        from("undertow:http://0.0.0.0:8080?matchOnUriPrefix=true")
                .routeId("hes-resource-route")
                .choice()
                    .when(header("CamelHttpPath").isEqualTo("/v1/ondemandread"))
                        .process(exchange -> {
                            var correlationIds = TraceUtil.populateCorrelationHeaders(exchange);

                            LOG.info("HES resource route invoked with traceId={}, trackingId={}",
                                    correlationIds.traceId(), correlationIds.trackingId());
                            StructuredLogger.info(exchange, "HES_RESOURCE_REQUEST", "HES on-demand read request received");
                        })
                        .bean(new HESService(), "invokeOnDemandRead")
                        .process(exchange -> {
                            StructuredLogger.info(exchange, "HES_RESOURCE_RESPONSE", "HES resource response prepared");
                            LOG.info("HES resource response prepared");
                        })
                    .otherwise()
                        .stop()
                .end();
    }

   
}
