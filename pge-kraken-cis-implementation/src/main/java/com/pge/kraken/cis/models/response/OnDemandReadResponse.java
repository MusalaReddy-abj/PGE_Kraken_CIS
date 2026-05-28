package com.pge.kraken.cis.models.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OnDemandReadResponse {

    private String requestId;
    private String status;
    private String message;
    private LocalDateTime processedAt;
}
