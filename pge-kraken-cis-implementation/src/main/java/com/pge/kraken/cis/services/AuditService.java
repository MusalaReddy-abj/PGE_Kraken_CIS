package com.pge.kraken.cis.services;

import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.models.event.AuditEvent;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class AuditService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditService.class);

    public void auditExchange(Exchange exchange) {
        LOG.info("Auditing exchange");
        StructuredLogger.info(exchange, "AUDIT_SERVICE", "Auditing exchange");

        AuditEvent event = new AuditEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType("AUDIT");
        event.setMessage("Processed exchange successfully");
        event.setTimestamp(LocalDateTime.now());

        exchange.getMessage().setBody(event);
        LOG.info("Audit event recorded successfully");
    }
}
