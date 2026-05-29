package com.pge.kraken.cis.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KongConfig {

    private final String host;
    private final String eventsResource;
    private final String defaultResource;
    private final String contentTypeXml;
    private final String contentTypeJson;

    public KongConfig() {
        Properties p = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("kong.properties")) {
            if (is != null) {
                p.load(is);
            }
        } catch (IOException ignored) {
        }

        String env = System.getenv("KONG_SERVICE_URL");
        String prop = System.getProperty("kong.service.url");

        this.host = p.getProperty("kong.host",
                env != null && !env.isBlank() ? env : (prop != null && !prop.isBlank() ? prop : "http://localhost:9000"));
        this.eventsResource = p.getProperty("kong.events.resource", "/deviceeventseeder");
        this.defaultResource = p.getProperty("kong.default.resource", "/deviceeventseeder");
        this.contentTypeXml = p.getProperty("kong.content.type.xml", "text/xml");
        this.contentTypeJson = p.getProperty("kong.content.type.json", "application/json");
    }

    public String getHost() {
        return host;
    }

    public String getEventsResource() {
        return eventsResource;
    }

    public String getDefaultResource() {
        return defaultResource;
    }

    public String getContentTypeXml() {
        return contentTypeXml;
    }

    public String getContentTypeJson() {
        return contentTypeJson;
    }
}
