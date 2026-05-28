package com.pge.kraken.cis.configs;

import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;

public class SSLConfig {

    public SSLContextParameters sslContextParameters() {
        KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
        keyStoreParameters.setResource("classpath:ssl/keystore.jks");
        keyStoreParameters.setPassword("changeit");

        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
        keyManagersParameters.setKeyStore(keyStoreParameters);
        keyManagersParameters.setKeyPassword("changeit");

        KeyStoreParameters trustStoreParameters = new KeyStoreParameters();
        trustStoreParameters.setResource("classpath:ssl/truststore.jks");
        trustStoreParameters.setPassword("changeit");

        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
        trustManagersParameters.setKeyStore(trustStoreParameters);

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(keyManagersParameters);
        sslContextParameters.setTrustManagers(trustManagersParameters);
        return sslContextParameters;
    }
}
