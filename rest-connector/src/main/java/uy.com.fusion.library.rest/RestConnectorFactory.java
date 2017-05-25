package uy.com.fusion.library.rest;

import java.util.EnumSet;

import javax.net.ssl.SSLContext;

import uy.com.fusion.library.rest.config.RestConnectorConfig;
import uy.com.fusion.library.rest.interceptors.impl.CacheInterceptor;
import uy.com.fusion.library.rest.serializers.json.JsonConfig;

@SuppressWarnings("deprecation")
public class RestConnectorFactory {

    @Deprecated
    public static RestConnector createRestConnector(String protocol, String host, String baseUrl,
                                                    Boolean usefusionHeaders, String xClient, String version, RestConnectorConfig restConnectorConfig) {
        return RestConnectorFactory.createRestConnector(protocol, host, baseUrl, usefusionHeaders, xClient, version,
                restConnectorConfig, null);
    }

    @Deprecated
    public static RestConnector createRestConnector(String protocol, String host, String baseUrl,
                                                    Boolean usefusionHeaders, String xClient, String version, RestConnectorConfig restConnectorConfig,
                                                    SSLContext sslContext) {

        RestConnector restConnector = RestConnector.createfusionRestConnector(protocol, host, restConnectorConfig,
                sslContext);

        return restConnector;
    }

    private String protocol;
    private String host;
    private String baseUrl;
    private Boolean usefusionHeaders;
    private String xClient;
    private String version;
    private Integer maxConnections;
    private Long connectionTimeout;
    private Long readTimeout;
    private Long idleConnectionTimeout;

    private Integer cacheSize;
    private Integer cacheConcurrencyLevel;
    private EnumSet<CacheInterceptor.CacheMethods> supportedCacheMethods;
    private JsonConfig jsonConfig;
    private Integer requestMaxRetries;

    public RestConnector createObject() {
        RestConnectorConfig.ConfigBuilder builder = RestConnectorConfig.createBuilder();
        if (this.maxConnections != null) {
            builder.maxConnections(this.maxConnections);
        }
        if (this.connectionTimeout != null) {
            builder.connectionTimeout(this.connectionTimeout);
        }
        if (this.readTimeout != null) {
            builder.readTimeout(this.readTimeout);
        }
        if (this.idleConnectionTimeout != null) {
            builder.idleConnectionTimeout(this.idleConnectionTimeout);
        }
        if (this.cacheSize != null) {
            builder.cacheSize(this.cacheSize);
        }
        if (this.cacheConcurrencyLevel != null) {
            builder.cacheConcurrencyLevel(this.cacheConcurrencyLevel);
        }
        if (this.supportedCacheMethods != null) {
            builder.supportedCacheMethods(this.supportedCacheMethods);
        }
        if (this.jsonConfig != null) {
            builder.jsonConfig(this.jsonConfig);
        }
        if (this.requestMaxRetries != null) {
            builder.requestMaxRetries(this.requestMaxRetries);
        }

        return createRestConnector(this.protocol, this.host, this.baseUrl, this.usefusionHeaders, this.xClient,
                this.version, builder.build());
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUsefusionHeaders(Boolean usefusionHeaders) {
        this.usefusionHeaders = usefusionHeaders;
    }

    public void setxClient(String xClient) {
        this.xClient = xClient;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setCacheConcurrencyLevel(Integer cacheConcurrencyLevel) {
        this.cacheConcurrencyLevel = cacheConcurrencyLevel;
    }

    public void setSupportedCacheMethods(EnumSet<CacheInterceptor.CacheMethods> supportedCacheMethods) {
        this.supportedCacheMethods = supportedCacheMethods;
    }

    public void setJsonConfig(JsonConfig jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    public void setRequestMaxRetries(Integer requestMaxRetries) {
        this.requestMaxRetries = requestMaxRetries;
    }

    public void setIdleConnectionTimeout(Long idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

}
