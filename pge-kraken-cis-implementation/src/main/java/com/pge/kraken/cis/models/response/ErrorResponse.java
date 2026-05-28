package com.pge.kraken.cis.models.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
}
