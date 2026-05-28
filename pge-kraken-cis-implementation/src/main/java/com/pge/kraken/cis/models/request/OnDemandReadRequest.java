package com.pge.kraken.cis.models.request;

import lombok.Data;

@Data
public class OnDemandReadRequest {

    private String requestId;
    private String patientId;
    private String sourceSystem;
    private String payload;
}
