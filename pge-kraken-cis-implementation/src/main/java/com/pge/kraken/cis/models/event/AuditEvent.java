package com.pge.kraken.cis.models.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditEvent {

    private String eventId;
    private String eventType;
    private String message;
    private LocalDateTime timestamp;
}
