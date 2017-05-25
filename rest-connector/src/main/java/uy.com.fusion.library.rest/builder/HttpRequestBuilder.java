package uy.com.fusion.library.rest.builder;

import java.io.IOException;
import java.util.Map;

import com.newrelic.api.agent.Trace;

import uy.com.fusion.library.rest.HttpBodyParts;
import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.HttpResponse;
import uy.com.fusion.library.rest.HttpStreamingResponse;
import uy.com.fusion.library.rest.client.InnerHttpClient;
import uy.com.fusion.library.rest.client.metrics.MetricRegistry;
import uy.com.fusion.library.rest.compression.ContentEncoding;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptors;
import uy.com.fusion.library.rest.multipart.Part;
import uy.com.fusion.library.rest.serializers.Serializers;
import uy.com.fusion.library.rest.serializers.StreamSerializers;

public class HttpRequestBuilder {

    final private HttpMethod method;
    final private String endpoint;
    final private String path;
    final private HttpHeaders headers;
    private Object body;
    private HttpBodyParts bodyParts;
    private PerRequestConfig perRequestConfig;

    final private InnerHttpClient client;
    final private Serializers serializers;
    final private StreamSerializers streamSerializers;
    final private Interceptors interceptors;

    public HttpRequestBuilder(InnerHttpClient client, Serializers serializers, StreamSerializers streamSerializers,
                              Interceptors interceptors, HttpMethod method, String endpoint, String path) {

        this.client = client;

        this.serializers = serializers;
        this.streamSerializers = streamSerializers;
        this.interceptors = interceptors;

        this.method = method;
        this.endpoint = endpoint;
        this.path = path;
        this.headers = new HttpHeaders();
        this.perRequestConfig = new PerRequestConfig();
    }

    private HttpRequestBuilder(HttpRequestBuilder that) {
        this.client = that.client;
        this.serializers = that.serializers;
        this.streamSerializers = that.streamSerializers;
        this.interceptors = that.interceptors;

        this.method = that.method;
        this.endpoint = that.endpoint;
        this.path = that.path;
        this.headers = that.headers.clone();
        this.body = that.body;
        this.bodyParts = that.bodyParts != null ? that.bodyParts.clone() : null;
        this.perRequestConfig = new PerRequestConfig(that.perRequestConfig);
    }

    public HttpRequestBuilder accept(String mimeType) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setAccept(mimeType);
        return that;
    }

    public HttpRequestBuilder acceptEncoding(ContentEncoding encoding) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setAcceptEncoding(encoding.getHeaderValue());
        return that;
    }

    @Deprecated
    public HttpRequestBuilder acceptEncoding(String encoding) {
        return acceptEncoding(ContentEncoding.fromHeaderValue(encoding));
    }

    public HttpRequestBuilder ifModifiedSince(String date) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setIfModifiedSince(date);
        return that;
    }

    public HttpRequestBuilder ifNoneMatch(String eTag) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setIfNoneMatch(eTag);
        return that;
    }

    /**
     * Set the encoding of your body. "gzip" or "snappy"
     */
    public HttpRequestBuilder encodeWith(ContentEncoding encoding) {
        if (!this.method.acceptsBody()) {
            throw new HttpRequestBuilderException(String.format("Method %s doesn't support body", this.method));
        }
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setContentEncoding(encoding.getHeaderValue());
        return that;
    }

    /**
     * Set the encoding of your body. "gzip" or "snappy"
     */
    @Deprecated
    public HttpRequestBuilder encodeWith(String encoding) {
        return encodeWith(ContentEncoding.fromHeaderValue(encoding));
    }

    /**
     * Set the content-type of your body. This will determine how the body will be serialized:
     * "application/json", "plain/text", "application/x-hessian"
     */
    public HttpRequestBuilder asContentType(String contentType) {
        if (!this.method.acceptsBody()) {
            throw new HttpRequestBuilderException(String.format("Method %s doesn't support body", this.method));
        }
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setContentType(contentType);
        return that;
    }

    /**
     * Set the body. If you don't want the rest-connector to serialize it, just put a byte[].
     */
    public HttpRequestBuilder withBody(Object body) {
        if (!this.method.acceptsBody()) {
            throw new HttpRequestBuilderException(String.format("Method %s doesn't support body", this.method));
        }
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.body = body;
        that.bodyParts = null;
        return that;
    }

    public HttpRequestBuilder withBodyPart(Part part) {
        if (!this.method.acceptsBody()) {
            throw new HttpRequestBuilderException(String.format("Method %s doesn't support body", this.method));
        }
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.body = null;
        if (that.bodyParts == null) {
            that.bodyParts = new HttpBodyParts();
        }
        that.bodyParts.getParts().add(part);
        return that;
    }

    // **************
    // Custom headers
    // **************
    public HttpRequestBuilder withHeader(String header, String value) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.set(header, value);
        return that;
    }

    public HttpRequestBuilder withHeaders(HttpHeaders headers) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        for (Map.Entry<String, String> entry : headers.toMap().entrySet()) {
            that.headers.set(entry.getKey(), entry.getValue());
        }
        return that;
    }

    public HttpRequestBuilder withDateHeader(String header, long value) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.headers.setDate(header, value);
        return that;
    }


    public HttpRequestBuilder withTimeout(long timeoutInMillis) {
        HttpRequestBuilder that = new HttpRequestBuilder(this);
        that.perRequestConfig.setTimeoutInMillis(timeoutInMillis);
        return that;
    }

    @Trace(metricName = "Custom/rest-connector/execute")
    public HttpResponse execute() throws IOException {
        HttpRequestContext httpRequest = this.createHttpRequestContext();

        httpRequest.setMetricRegistryBuilder(MetricRegistry.createBuilder(this.client));

        HttpResponseContext responseContext = this.interceptors.interceptAndExecute(httpRequest, this.client);
        return new HttpResponse(this.serializers, responseContext.getStatus(), responseContext.getHeaders(),
                responseContext.readAndGetBodyInputStream());
    }

    @Trace(metricName = "Custom/rest-connector/execute")
    public HttpStreamingResponse executeForStreaming() throws IOException {
        HttpRequestContext httpRequest = this.createHttpRequestContext();

        httpRequest.setForStreaming(true);
        httpRequest.setMetricRegistryBuilder(MetricRegistry.createBuilder(this.client));

        HttpResponseContext responseContext = this.interceptors.interceptAndExecute(httpRequest, this.client);
        return new HttpStreamingResponse(this.serializers, this.streamSerializers, responseContext.getStatus(),
                responseContext.getHeaders(), responseContext.getBodyInputStream());
    }

    private HttpRequestContext createHttpRequestContext() throws IOException {
        HttpRequestContext httpRequest = null;
        if (this.bodyParts != null) {
            httpRequest = this.serializers.serialize(this.method, this.endpoint, this.path, this.headers,
                    this.perRequestConfig, this.bodyParts);
        } else {
            httpRequest = this.serializers.serialize(this.method, this.endpoint, this.path, this.headers,
                    this.perRequestConfig, this.body);
        }
        return httpRequest;
    }
}
