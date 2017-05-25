package uy.com.fusion.library.rest.interceptors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uy.com.fusion.library.rest.HttpBodyParts;
import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.builder.PerRequestConfig;
import uy.com.fusion.library.rest.client.metrics.MetricRegistry;

public class HttpRequestContext {

    private HttpMethod method;
    private String endpoint;
    private String path;
    private HttpHeaders headers;
    private byte[] body;
    private HttpBodyParts bodyParts;
    private Map<String, Object> context;
    private PerRequestConfig perRequestConfig;
    private MetricRegistry.MetricRegistryBuilder metricRegistryBuilder;
    private boolean forStreaming;

    public HttpRequestContext(HttpMethod method, String endpoint, String path, HttpHeaders headers,
        PerRequestConfig perRequestConfig, byte[] body) {
        this(method, endpoint, path, headers, perRequestConfig, body, null);
    }

    public HttpRequestContext(HttpMethod method, String endpoint, String path, HttpHeaders headers,
        PerRequestConfig perRequestConfig, byte[] body, HttpBodyParts bodyParts) {
        this.method = method;
        this.endpoint = endpoint;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.context = new HashMap<>();
        this.bodyParts = bodyParts;
        this.perRequestConfig = perRequestConfig;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, Object> getAllContext() {
        return Collections.unmodifiableMap(this.context);
    }

    public void setAllContext(Map<String, Object> context) {
        this.context.putAll(context);
    }

    public Object getContext(String context) {
        return this.context.get(context);
    }

    public void setContext(String name, Object context) {
        this.context.put(name, context);
    }

    public PerRequestConfig getPerRequestConfig() {
        return this.perRequestConfig;
    }

    public void setPerRequestConfig(PerRequestConfig perRequestConfig) {
        this.perRequestConfig = perRequestConfig;
    }

    public MetricRegistry.MetricRegistryBuilder getMetricRegistryBuilder() {
        return this.metricRegistryBuilder;
    }

    public void setMetricRegistryBuilder(MetricRegistry.MetricRegistryBuilder metricRegistryBuilder) {
        this.metricRegistryBuilder = metricRegistryBuilder;
    }

    public boolean isForStreaming() {
        return this.forStreaming;
    }

    public void setForStreaming(boolean forStreaming) {
        this.forStreaming = forStreaming;
    }

    @Override
    public HttpRequestContext clone() {
        HttpRequestContext cloned = new HttpRequestContext(this.method, this.endpoint, this.path, this.headers.clone(),
            this.perRequestConfig, this.body, this.bodyParts.clone());
        cloned.context.putAll(this.context);
        cloned.metricRegistryBuilder = this.metricRegistryBuilder;
        cloned.forStreaming = this.forStreaming;
        return cloned;
    }

    @Override
    public String toString() {
        return "HttpRequestContext [method=" + this.method + ", endpoint=" + this.endpoint + ", path=" + this.path
            + ", headers=" + this.headers + ", body=" + (this.body != null ? new String(this.body) : "null") + ", bodyParts="
            + (this.bodyParts != null ? this.bodyParts.getParts().size() + " parts" : "null") + "]";
    }

    public HttpBodyParts getBodyParts() {
        return this.bodyParts;
    }

    public void setBodyParts(HttpBodyParts bodyParts) {
        this.bodyParts = bodyParts;
    }
}
