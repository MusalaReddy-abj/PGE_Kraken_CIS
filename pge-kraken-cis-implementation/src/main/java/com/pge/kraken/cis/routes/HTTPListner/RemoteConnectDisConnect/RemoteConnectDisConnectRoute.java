package com.pge.kraken.cis.routes.HTTPListner.RemoteConnectDisConnect;

import com.pge.kraken.cis.configs.HttpListnerConfig;
import com.pge.kraken.cis.logging.StructuredLogger;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteConnectDisConnectRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteConnectDisConnectRoute.class);

    private static final String ACKNOWLEDGEMENT_RESPONSE = """
            <responseDetail xmlns="http://xmlns.oracle.com/ouaf/iec">
                   <instanceID>118336964</instanceID>
                   <reply>
                      <replyCode>0.0</replyCode>
                      <error>
                          <error/>
                      </error>
                   </reply>
            </responseDetail>
            """;

    @Override
    public void configure() {
        HttpListnerConfig cfg = HttpListnerConfig.fromProperties();
        String endpointUri = String.format("undertow:http://%s:%d%s?httpMethodRestrict=POST",
                cfg.getHost(), cfg.getPort(), cfg.getRemoteConnectDisconnectPath());

        from(endpointUri)
                .routeId("mdm-connect-disconnect-route")
                .process(exchange -> {
                    LOG.info("MDM connect/disconnect request received");
                    StructuredLogger.info(exchange, "MDM_CONNECT_DISCONNECT_REQUEST",
                            "MDM connect/disconnect XML request received");
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/xml"))
                .setBody(constant(ACKNOWLEDGEMENT_RESPONSE))
                .process(exchange -> {
                    StructuredLogger.info(exchange, "MDM_CONNECT_DISCONNECT_ACK",
                            "MDM connect/disconnect acknowledgement response prepared");
                    LOG.info("MDM connect/disconnect acknowledgement response prepared");
                });
    }
}
