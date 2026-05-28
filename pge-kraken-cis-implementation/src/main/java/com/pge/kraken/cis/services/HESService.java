package com.pge.kraken.cis.services;

import com.pge.kraken.cis.models.response.OnDemandReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class HESService {

    private static final Logger LOG = LoggerFactory.getLogger(HESService.class);

    public OnDemandReadResponse invokeOnDemandRead(Object payload) {
        LOG.info("Invoking HES on-demand read service");

        OnDemandReadResponse response = new OnDemandReadResponse();
        response.setRequestId("placeholder-request-id");
        response.setStatus("SUCCESS");
        response.setMessage("HES on-demand read completed");
        response.setProcessedAt(LocalDateTime.now());

        LOG.info("HES on-demand read service completed successfully");
        return response;
    }
}
