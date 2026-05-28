package com.pge.kraken.cis.validators;

import com.pge.kraken.cis.models.request.OnDemandReadRequest;
import com.pge.kraken.cis.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestValidator {

    private static final Logger LOG = LoggerFactory.getLogger(RequestValidator.class);

    public void validate(OnDemandReadRequest request) {
        LOG.info("Validating on-demand read request");

        if (request == null) {
            LOG.warn("Validation failed: request payload is null");
            throw new IllegalArgumentException("Request payload cannot be null");
        }
        if (ValidationUtil.isBlank(request.getPatientId())) {
            LOG.warn("Validation failed: patientId is required");
            throw new IllegalArgumentException("patientId is required");
        }

        LOG.info("Validation passed for on-demand read request");
    }
}
