package com.pge.kraken.cis.processors;

import com.pge.kraken.cis.mapper.D1DeviceEventSoapMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapEnvelopeProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(SoapEnvelopeProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getIn().getBody(String.class);
        LOG.info("Wrapping message in SOAP envelope");

        String soapPayload = D1DeviceEventSoapMapper.toSoapEnvelope(payload);
        exchange.getIn().setBody(soapPayload);
    }
}
