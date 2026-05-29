package com.pge.kraken.cis.configs;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

@Data
public class HttpListnerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(HttpListnerConfig.class);

    private String host;
    private int port;
    private String remoteConnectDisconnectPath;
    private String rcdcHesResponsePath;

    public static HttpListnerConfig fromProperties() {
        Properties props = new Properties();
        try (InputStream in = HttpListnerConfig.class.getClassLoader().getResourceAsStream("httplistner.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            LOG.warn("Failed to load HTTP listener configuration from httplistner.properties; using defaults", e);
        }

        HttpListnerConfig cfg = new HttpListnerConfig();
        cfg.host = props.getProperty("http.listner.host", "0.0.0.0");
        cfg.port = Integer.parseInt(props.getProperty("http.listner.port", "8080"));
        cfg.remoteConnectDisconnectPath = props.getProperty(
                "http.listner.remote.connect.disconnect.path",
                "/v1/mdm/connect-disconnect");
        cfg.rcdcHesResponsePath = props.getProperty(
                "http.listner.rcdc.hes.response.path",
                "/v1/hes/rcdc-response");
        return cfg;
    }
}
