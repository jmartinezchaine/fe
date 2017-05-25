package uy.com.fusion.library.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.builder.HttpRequestBuilder;
import uy.com.fusion.library.rest.client.InnerHttpClient;
import uy.com.fusion.library.rest.client.InnerHttpClientBuilder;
import uy.com.fusion.library.rest.client.metrics.MetricRegistryListener;
import uy.com.fusion.library.rest.config.RestConnectorConfig;
import uy.com.fusion.library.rest.interceptors.Interceptors;
import uy.com.fusion.library.rest.serializers.Serializers;
import uy.com.fusion.library.rest.serializers.StreamSerializers;
import uy.com.fusion.library.rest.utils.ShutdownHook;

public class RestConnector {

    /**
     * Serializers: {String}
     * Interceptors: []
     */
    public static final RestConnector createSimple(String host) {
        return createSimple("http", host);
    }

    /**
     * Serializers: {String}
     * Interceptors: []
     */
    public static final RestConnector createSimple(String protocol, String host) {
        return createSimple(protocol, host, RestConnectorConfig.createBuilder().build());
    }

    /**
     * Serializers: {String}
     * Interceptors: []
     */
    public static final RestConnector createSimple(String protocol, String host, RestConnectorConfig config) {
        return new RestConnector(protocol, host, config, Serializers.createString(), StreamSerializers.createString(), Interceptors.create());
    }

    /**
     * Serializers: {Json}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse]
     */
    public static final RestConnector createWithJsonGzip(String host) {
        return createWithJsonGzip("http", host);
    }

    /**
     * Serializers: {Json}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse]
     */
    public static final RestConnector createWithJsonGzip(String protocol, String host) {
        return createWithJsonGzip(protocol, host, RestConnectorConfig.createDefault());
    }

    /**
     * Serializers: {Json}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse]
     */
    public static final RestConnector createWithJsonGzip(String protocol, String host, RestConnectorConfig config) {
        return new RestConnector(protocol, host, config, Serializers.createJson(config), StreamSerializers.createJson(config),
                        Interceptors.createJsonGzipfusion());
    }

    /**
     * Serializers: {String, Json, Hessian}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse, SnappyRequest , SnappyResponse, Cache, Routing]
     */
    public static final RestConnector createfusionRestConnector(String host) {
        return createfusionRestConnector("http", host);
    }

    /**
     * Serializers: {String, Json, Hessian}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse, SnappyRequest , SnappyResponse, Cache, Routing]
     */
    public static final RestConnector createfusionRestConnector(String protocol, String host) {
        return createfusionRestConnector(protocol, host, RestConnectorConfig.createBuilder().build());
    }

    /**
     * Serializers: {String, Json, Hessian}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse, SnappyRequest , SnappyResponse, Cache, Routing]
     */
    public static final RestConnector createfusionRestConnector(String protocol, String host, RestConnectorConfig config) {
        return createfusionRestConnector(protocol, host, config, null);
    }

    /**
     * Serializers: {String, Json, Hessian}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse, SnappyRequest , SnappyResponse, Cache, Routing]
     */
    public static final RestConnector createfusionRestConnector(String protocol, String host, RestConnectorConfig config, SSLContext sslContext) {
        return new RestConnector(protocol, host, config, Serializers.createfusion(config), StreamSerializers.createfusion(config),
                        Interceptors.createJsonGzipfusion(), sslContext);
    }

    /**
     * Serializers: {String, Json, Hessian}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse, SnappyRequest , SnappyResponse, Cache, Routing]
     */
    public static final RestConnector createNonJodafusionRestConnector(String protocol, String host, RestConnectorConfig config) {
        return createNonJodafusionRestConnector(protocol, host, config, null);
    }

    /**
     * Serializers: {String, Json, Hessian}
     * Interceptors: [AcceptJson, GzipRequest, GzipResponse, SnappyRequest , SnappyResponse, Cache, Routing]
     */
    public static final RestConnector createNonJodafusionRestConnector(String protocol, String host, RestConnectorConfig config, SSLContext sslContext) {
        return new RestConnector(protocol, host, config, Serializers.createNonJodafusion(config), StreamSerializers.createNonJodafusion(config),
                        Interceptors.createJsonGzipfusion(), sslContext);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RestConnector.class);

    private String endpoint;
    private InnerHttpClient client;
    private Serializers serializers;
    private StreamSerializers streamSerializers;
    private Interceptors interceptors;
    private AtomicBoolean isShutdown = new AtomicBoolean(false);

    public RestConnector(String protocol, String host, RestConnectorConfig config, Serializers serializers, StreamSerializers streamSerializers,
                    Interceptors interceptors) {
        this(protocol, host, config, serializers, streamSerializers, interceptors, null);
    }

    public RestConnector(String protocol, String host, RestConnectorConfig config, Serializers serializers, StreamSerializers streamSerializers,
                    Interceptors interceptors, SSLContext sslContext) {
        if (host == null) {
            String[] split = config.getEndpoint().split("://");
            protocol = split[0];
            host = split[1];
        } else {
            config.setEndpoint(protocol + "://" + host);
        }

        try {
            URL url = new URL(protocol + "://" + host);
            if ((url.getPath() != null && !url.getPath().isEmpty()) || (url.getQuery() != null && !url.getQuery().isEmpty())) {
                throw new IllegalArgumentException("Bad host format. Use '(user:password@)host(:port)'.");
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Bad host format. Use '(user:password@)host(:port)'.");
        }

        if (serializers == null) {
            throw new IllegalArgumentException("'serializers' can not be null");
        }

        if (interceptors == null) {
            throw new IllegalArgumentException("'interceptors' can not be null");
        }


        this.endpoint = protocol + "://" + host;
        InnerHttpClientBuilder builder = config.getInnerHttpClientBuilder();
        builder.setRestConnectorConfig(config);
        builder.setSslContext(sslContext);
        this.client = builder.build();
        this.serializers = serializers;
        this.streamSerializers = streamSerializers;
        this.interceptors = interceptors;
        this.init(config);
    }

    @Override
    /**
     * JVM will call this before garbage collecting this object
     */ protected void finalize() throws Throwable {
        super.finalize();
        this.shutdown();
    }

    private void init(RestConnectorConfig config) {
        if (config.isShutdownHookEnabled()) {
            ShutdownHook.attachShutdownHookTo(this);
        }
    }

    public void shutdown() {
        if (this.isShutdown.compareAndSet(false, true)) {
            LOGGER.info("Shutting down rest connector");
            this.client.shutdown();
        }
    }

    // ***************
    // getters & setters
    // ***************

    @Deprecated
    public void setSerializers(Serializers serializers) {
        this.serializers = serializers;
    }

    public Serializers getSerializers() {
        return this.serializers;
    }

    @Deprecated
    public void setStreamSerializers(StreamSerializers streamSerializers) {
        this.streamSerializers = streamSerializers;
    }

    public StreamSerializers getStreamSerializers() {
        return this.streamSerializers;
    }

    @Deprecated
    public void setInterceptors(Interceptors interceptors) {
        this.interceptors = interceptors;
    }

    public Interceptors getInterceptors() {
        return this.interceptors;
    }

    // ***************
    // Metrics Listeners
    // ***************

    public void addMetricListener(MetricRegistryListener listener) {
        this.client.addListener(listener);
    }

    // ***************
    // Execute Methods
    // ***************

    public HttpRequestBuilder head(String path) {
        return this.create(HttpMethod.HEAD, path);
    }

    public HttpRequestBuilder head(Query query) {
        return this.create(HttpMethod.HEAD, query);
    }

    public HttpRequestBuilder get(String path) {
        return this.create(HttpMethod.GET, path);
    }

    public HttpRequestBuilder get(Query query) {
        return this.create(HttpMethod.GET, query);
    }

    public HttpRequestBuilder put(String path) {
        return this.create(HttpMethod.PUT, path);
    }

    public HttpRequestBuilder put(Query query) {
        return this.create(HttpMethod.PUT, query);
    }

    public HttpRequestBuilder post(String path) {
        return this.create(HttpMethod.POST, path);
    }

    public HttpRequestBuilder post(Query query) {
        return this.create(HttpMethod.POST, query);
    }

    public HttpRequestBuilder delete(String path) {
        return this.create(HttpMethod.DELETE, path);
    }

    public HttpRequestBuilder delete(Query query) {
        return this.create(HttpMethod.DELETE, query);
    }

    public HttpRequestBuilder patch(String path) {
        return this.create(HttpMethod.PATCH, path);
    }

    public HttpRequestBuilder patch(Query query) {
        return this.create(HttpMethod.PATCH, query);
    }

    public HttpRequestBuilder options(String path) {
        return this.create(HttpMethod.OPTIONS, path);
    }

    public HttpRequestBuilder options(Query query) {
        return this.create(HttpMethod.OPTIONS, query);
    }

    public HttpRequestBuilder trace(String path) {
        return this.create(HttpMethod.TRACE, path);
    }

    public HttpRequestBuilder trace(Query query) {
        return this.create(HttpMethod.TRACE, query);
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    private HttpRequestBuilder create(HttpMethod method, String path) {
        this.checkPath(path);
        return new HttpRequestBuilder(this.client, this.serializers, this.streamSerializers, this.interceptors, method, this.endpoint, path);
    }

    private HttpRequestBuilder create(HttpMethod method, Query query) {
        return this.create(method, query.toString());
    }

    private void checkPath(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("'path' has to start with '/'.");
        }
    }

}
