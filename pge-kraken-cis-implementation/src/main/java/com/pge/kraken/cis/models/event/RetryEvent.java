package com.pge.kraken.cis.models.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RetryEvent {

    private String correlationId;
    private int retryCount;
    private String status;
    private LocalDateTime timestamp;
}
