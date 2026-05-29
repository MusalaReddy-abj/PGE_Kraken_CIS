package com.pge.kraken.cis.clients;

import com.pge.kraken.cis.configs.KongConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KongApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(KongApiClient.class);

    private final String baseUrl;
    private final String defaultResource;
    private final HttpClient httpClient;

    public KongApiClient() {
        this(new KongConfig());
    }

    public KongApiClient(KongConfig config) {
        this.baseUrl = config.getHost();
        this.defaultResource = config.getDefaultResource();
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> postDeviceEvent(String resourcePath, String payload) throws IOException, InterruptedException {
        return postDeviceEvent(resourcePath, payload, null);
    }

    public HttpResponse<String> postDeviceEvent(String resourcePath, String payload, String contentType) throws IOException, InterruptedException {
        String path = resourcePath != null && !resourcePath.isBlank() ? resourcePath : defaultResource;
        if (!path.startsWith("http")) {
            path = (baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl)
                    + (path.startsWith("/") ? path : "/" + path);
        }
        String ct = contentType != null && !contentType.isBlank() ? contentType : "application/xml";
        String payloadPreview = payload != null ? (payload.length() > 200 ? payload.substring(0, 200) + "..." : payload) : "<empty>";
        LOG.info("KongApiClient posting to {} with Content-Type={} payloadPreview={}", path, ct, payloadPreview);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(path))
                .header("Content-Type", ct)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
