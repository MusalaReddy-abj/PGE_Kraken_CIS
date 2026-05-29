package com.pge.kraken.cis.routes.HTTPListner.RemoteConnectDisConnect;

import com.pge.kraken.cis.configs.HttpListnerConfig;
import com.pge.kraken.cis.configs.KafkaPropertiesConfig;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.services.KafkaService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RCDCHESResponseRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RCDCHESResponseRoute.class);

    @Override
    public void configure() {
        HttpListnerConfig httpCfg = HttpListnerConfig.fromProperties();
        KafkaPropertiesConfig kafkaCfg = KafkaPropertiesConfig.fromProperties();
        KafkaService kafkaService = new KafkaService();
        String endpointUri = String.format("undertow:http://%s:%d%s?httpMethodRestrict=POST",
                httpCfg.getHost(), httpCfg.getPort(), httpCfg.getRcdcHesResponsePath());

        from(endpointUri)
                .routeId("rcdc-hes-response-route")
                .process(exchange -> {
                    LOG.info("RCDC HES response received");
                    StructuredLogger.info(exchange, "RCDC_HES_RESPONSE_RECEIVED",
                            "RCDC HES JSON response received");
                })
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    String payload = exchange.getIn().getBody(String.class);
                    kafkaService.sendMessage(kafkaCfg.getRcdcHesResponseTopic(), payload, exchange);
                    StructuredLogger.info(exchange, "RCDC_HES_RESPONSE_SENT_TO_KAFKA",
                            String.format("RCDC HES response sent to Kafka topic %s",
                                    kafkaCfg.getRcdcHesResponseTopic()));
                    LOG.info("RCDC HES response sent to Kafka topic {}", kafkaCfg.getRcdcHesResponseTopic());
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setBody(constant("{\"status\":\"ACCEPTED\"}"));
    }
}
