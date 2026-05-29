package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.clients.KongApiClient;
import com.pge.kraken.cis.configs.KafkaPropertiesConfig;
import com.pge.kraken.cis.configs.KongConfig;
import com.pge.kraken.cis.constants.AppConstants;
import com.pge.kraken.cis.constants.KafkaTopics;
import com.pge.kraken.cis.exceptions.TechnicalException;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.services.KafkaService;
import com.pge.kraken.cis.utils.RetryUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.time.Duration;

public class KongForwardingProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(KongForwardingProcessor.class);
    private final KongConfig kongConfig = new KongConfig();
    private final KafkaPropertiesConfig kafkaPropertiesConfig = KafkaPropertiesConfig.fromProperties();
    private final KongApiClient kongClient = new KongApiClient(kongConfig);
    private final KafkaService kafkaService = new KafkaService();

    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getIn().getBody(String.class);
        LOG.info("Forwarding message to Kong via KongApiClient");

        String resourcePath = exchange.getIn().getHeader("kong.resource.path", String.class);
        if (resourcePath == null || resourcePath.isBlank()) {
            resourcePath = kongConfig.getEventsResource();
        }

        String contentType = exchange.getIn().getHeader("kong.content.type", String.class);
        if (contentType == null || contentType.isBlank()) {
            contentType = kongConfig.getContentTypeXml();
        }

        final String resolvedResourcePath = resourcePath;
        final String resolvedContentType = contentType;

        try {
            HttpResponse<String> response = RetryUtil.executeWithRetry(
                    () -> {
                        HttpResponse<String> httpResponse = kongClient.postDeviceEvent(resolvedResourcePath, payload, resolvedContentType);
                        int statusCode = httpResponse.statusCode();
                        if (RetryUtil.isRetryableHttpStatusCode(statusCode)) {
                            throw new RetryUtil.RetryableHttpStatusCodeException(statusCode, httpResponse.body());
                        }
                        return httpResponse;
                    },
                    AppConstants.DEFAULT_RETRY_COUNT,
                    Duration.ofSeconds(2),
                    RetryUtil::isRetryableException,
                    context -> StructuredLogger.warn(exchange, "KONG_RETRY_ATTEMPT",
                            String.format("Kong retryable status on attempt %d/%d: %s",
                                    context.getAttempt(), context.getMaxAttempts(), context.getException().getMessage())));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                StructuredLogger.warn(exchange, "KONG_RESPONSE_ERROR",
                        String.format("Kong returned error status %d, routing message to DLQ", response.statusCode()));
                routeToDlq(exchange, payload, String.format("Kong response status %d", response.statusCode()));
                return;
            }

            StructuredLogger.info(exchange, "KONG_FORWARD_SUCCESS",
                    String.format("Forwarded message to Kong endpoint, status=%d", response.statusCode()));
            exchange.getIn().setHeader("KongResponse", response.body());
        } catch (Exception e) {
            RetryUtil.RetryableHttpStatusCodeException statusException = RetryUtil.findRetryableHttpStatusCodeException(e);
            if (statusException != null) {
                StructuredLogger.warn(exchange, "KONG_RETRY_EXHAUSTED",
                        String.format("Kong returned retryable status %d after retries", statusException.getStatusCode()));
                routeToDlq(exchange, payload,
                        String.format("Kong response status %d after retries", statusException.getStatusCode()));
                return;
            }

            if (RetryUtil.isRetryableException(e)) {
                StructuredLogger.warn(exchange, "KONG_RETRY_EXHAUSTED",
                        String.format("Kong connectivity failed after retries: %s", e.getMessage()));
                routeToDlq(exchange, payload, String.format("Kong connectivity failure after retries: %s", e.getMessage()));
                return;
            }

            StructuredLogger.error(exchange, "KONG_FORWARD_ERROR",
                    String.format("Failed to forward message to Kong: %s", e.getMessage()), e);
            throw new TechnicalException("Failed to forward message to Kong", e).withContext(exchange);
        }
    }

    private boolean isRetryableException(Exception e) {
        return RetryUtil.isRetryableException(e);
    }

    private void routeToDlq(Exchange exchange, String message, String reason) {
        String dlqTopic = kafkaPropertiesConfig.getDeviceEventsDlqTopic();
        if (dlqTopic == null || dlqTopic.isBlank()) {
            dlqTopic = KafkaTopics.DLQ_TOPIC;
        }

        kafkaService.sendMessage(dlqTopic, message, exchange);
        StructuredLogger.warn(exchange, "KONG_DLQ_ROUTED",
                String.format("Message routed to DLQ topic %s due to Kong failure: %s", dlqTopic, reason));
    }
}
