package uy.com.fusion.library.rest.config;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import uy.com.fusion.library.rest.client.InnerHttpClientBuilder;
import uy.com.fusion.library.rest.client.apache.InnerApacheHttpClientBuilder;
import uy.com.fusion.library.rest.interceptors.impl.CacheInterceptor;
import uy.com.fusion.library.rest.serializers.json.JsonConfig;

public class RestConnectorConfig {

    // //////////////////////////////////////////////
    // FACTORY METHODS
    // //////////////////////////////////////////////

    public static ConfigBuilder createBuilder() {
        return new ConfigBuilder();
    }

    public static ConfigBuilder createBuilder(Config config) {
        return new ConfigBuilder(config);
    }

    public static ConfigBuilder createBuilder(RestConnectorConfig restConnectorConfig) {
        return new ConfigBuilder(restConnectorConfig);
    }

    public static RestConnectorConfig createDefault() {
        return new ConfigBuilder().build();
    }


    public static RestConnectorConfig createFrom(String configFilePath) {
        return createFrom(ConfigFactory.load(configFilePath));
    }

    public static RestConnectorConfig createFrom(Config config) {
        return new ConfigBuilder(config).build();
    }


    // //////////////////////////////////////////////
    // ATTRIBUTES
    // //////////////////////////////////////////////

    private Config config;
    private String endpoint;
    private boolean shutdownHookEnabled;
    private int maxConnections;
    private long connectionTimeout;
    private long readTimeout;
    private long idleConnectionTimeout;
    private boolean cacheEnabled;
    private int cacheSize;
    private int cacheConcurrencyLevel;
    private EnumSet<CacheInterceptor.CacheMethods> supportedCacheMethods;
    private JsonConfig jsonConfig;
    private InnerHttpClientBuilder innerHttpClientBuilder;
    private int requestMaxRetries;
    private long validateAfterInactivity;

    private RestConnectorConfig(Config config, String endpoint, boolean shutdownHookEnabled, int maxConnections,
        long connectionTimeout, long readTimeout, long idleConnectionTimeout, boolean cacheEnabled, int cacheSize,
        int cacheConcurrencyLevel, EnumSet<CacheInterceptor.CacheMethods> supportedCacheMethods, JsonConfig jsonConfig, int requestMaxRetries,
        InnerHttpClientBuilder innerHttpClientBuilder, long validateAfterInactivity) {

        this.config = config;
        this.endpoint = endpoint;
        this.shutdownHookEnabled = shutdownHookEnabled;

        // connection pool
        this.maxConnections = maxConnections;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.idleConnectionTimeout = idleConnectionTimeout;

        // cache
        this.cacheEnabled = cacheEnabled;
        this.cacheSize = cacheSize;
        this.cacheConcurrencyLevel = cacheConcurrencyLevel;
        this.supportedCacheMethods = supportedCacheMethods;

        // json
        this.jsonConfig = jsonConfig;
        this.requestMaxRetries = requestMaxRetries;

        // Inner HTTP Client Customization
        this.innerHttpClientBuilder = innerHttpClientBuilder;
        this.validateAfterInactivity = validateAfterInactivity;
    }

    public Config getConfig() {
        return this.config;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public boolean isShutdownHookEnabled() {
        return this.shutdownHookEnabled;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public long getIdleConnectionTimeout() {
        return this.idleConnectionTimeout;
    }

    public boolean isCacheEnabled() {
        return this.cacheEnabled;
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    public int getCacheConcurrencyLevel() {
        return this.cacheConcurrencyLevel;
    }

    public EnumSet<CacheInterceptor.CacheMethods> getSupportedCacheMethods() {
        return this.supportedCacheMethods;
    }

    public JsonConfig getJsonConfig() {
        return this.jsonConfig;
    }

    public int getRequestMaxRetries() {
        return this.requestMaxRetries;
    }

    public long getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    /**
     * Config should be immutable. Choose better how to instanciate your objects.
     */
    @Deprecated
    public void setRequestMaxRetries(int requestMaxRetries) {
        this.requestMaxRetries = requestMaxRetries;
    }

    public InnerHttpClientBuilder getInnerHttpClientBuilder() {
        return this.innerHttpClientBuilder;
    }


    // //////////////////////////////////////////////
    // BUILDER
    // //////////////////////////////////////////////

    public static class ConfigBuilder {

        private Config config;
        private String endpoint;
        private boolean shutdownHookEnabled;
        private int maxConnections;
        private long connectionTimeout;
        private long readTimeout;
        private long idleConnectionTimeout;

        private boolean cacheEnabled;
        private int cacheSize;
        private int cacheConcurrencyLevel;
        private EnumSet<CacheInterceptor.CacheMethods> supportedCacheMethods;

        private JsonConfig jsonConfig;
        private int requestMaxRetries;
        private InnerHttpClientBuilder innerHttpClientBuilder;
        private long validateAfterInactivity;

        private ConfigBuilder() {
            this(ConfigFactory.load());
        }

        private ConfigBuilder(RestConnectorConfig config) {
            this.config = config.getConfig();
            this.endpoint = config.endpoint;
            this.shutdownHookEnabled = config.shutdownHookEnabled;
            this.maxConnections = config.maxConnections;
            this.connectionTimeout = config.connectionTimeout;
            this.readTimeout = config.readTimeout;
            this.idleConnectionTimeout = config.idleConnectionTimeout;

            this.cacheEnabled = config.cacheEnabled;
            this.cacheSize = config.cacheSize;
            this.cacheConcurrencyLevel = config.cacheConcurrencyLevel;
            this.supportedCacheMethods = config.supportedCacheMethods;

            this.jsonConfig = config.jsonConfig;
            this.requestMaxRetries = config.requestMaxRetries;
            this.innerHttpClientBuilder = config.innerHttpClientBuilder;
            this.validateAfterInactivity = config.validateAfterInactivity;
        }

        private ConfigBuilder(Config mainConfig) {

            this.config = mainConfig.withFallback(ConfigFactory.load());
            Config myConfig = this.config.getConfig("rest-connector");

            this.endpoint = myConfig.hasPath("endpoint") ? myConfig.getString("endpoint") : null;
            this.shutdownHookEnabled = myConfig.getBoolean("shutdown-hook-enabled");
            this.maxConnections = myConfig.getInt("max-connections");
            this.connectionTimeout = myConfig.getDuration("connection-timeout", TimeUnit.MILLISECONDS);
            this.readTimeout = myConfig.getDuration("read-timeout", TimeUnit.MILLISECONDS);
            this.idleConnectionTimeout = myConfig.getDuration("idle-connection-timeout", TimeUnit.MILLISECONDS);

            this.cacheEnabled = myConfig.getBoolean("cache.enabled");
            this.cacheSize = myConfig.getInt("cache.size");
            this.cacheConcurrencyLevel = myConfig.getInt("cache.concurrency-level");

            List<String> list = myConfig.getStringList("cache.supported-methods");
            this.supportedCacheMethods = EnumSet.noneOf(CacheInterceptor.CacheMethods.class);
            for (String s : list) {
                this.supportedCacheMethods.add(CacheInterceptor.CacheMethods.valueOf(s.toUpperCase()));
            }

            this.jsonConfig = JsonConfig.createFrom(myConfig.getConfig("json"));
            this.requestMaxRetries = myConfig.getInt("request.max-retries");

            this.validateAfterInactivity = myConfig.getDuration("validate-after-inactivity", TimeUnit.MILLISECONDS);
        }

        public ConfigBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public ConfigBuilder setShutdownHookEnabled(boolean shutdownHookEnabled) {
            this.shutdownHookEnabled = shutdownHookEnabled;
            return this;
        }

        public ConfigBuilder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public ConfigBuilder connectionTimeout(long connectionTimeoutInMillis) {
            this.connectionTimeout = connectionTimeoutInMillis;
            return this;
        }

        public ConfigBuilder readTimeout(long readTimeoutInMillis) {
            this.readTimeout = readTimeoutInMillis;
            return this;
        }

        public ConfigBuilder idleConnectionTimeout(long idleConnectionTimeoutInMillis) {
            this.idleConnectionTimeout = idleConnectionTimeoutInMillis;
            return this;
        }

        public ConfigBuilder cacheSize(int size) {
            this.cacheEnabled = true;
            this.cacheSize = size;
            return this;
        }

        public ConfigBuilder cacheConcurrencyLevel(int concurrencyLevel) {
            this.cacheEnabled = true;
            this.cacheConcurrencyLevel = concurrencyLevel;
            return this;
        }

        public ConfigBuilder supportedCacheMethods(EnumSet<CacheInterceptor.CacheMethods> supportedMethods) {
            this.cacheEnabled = true;
            this.supportedCacheMethods = supportedMethods;
            return this;
        }

        public ConfigBuilder jsonConfig(JsonConfig jsonConfig) {
            this.jsonConfig = jsonConfig;
            return this;
        }

        public ConfigBuilder requestMaxRetries(int requestMaxRetries) {
            this.requestMaxRetries = requestMaxRetries;
            return this;
        }

        public ConfigBuilder withValidateAfterInactivity(long validateAfterInactivity) {
            this.validateAfterInactivity = validateAfterInactivity;
            return this;
        }

        public ConfigBuilder innerHttpClientBuilder(InnerHttpClientBuilder innerHttpClientBuilder) {
            this.innerHttpClientBuilder = innerHttpClientBuilder;
            return this;
        }

        public RestConnectorConfig build() {
            return new RestConnectorConfig(this.config, this.endpoint, this.shutdownHookEnabled, this.maxConnections,
                this.connectionTimeout, this.readTimeout, this.idleConnectionTimeout, this.cacheEnabled, this.cacheSize,
                this.cacheConcurrencyLevel, this.supportedCacheMethods, this.jsonConfig, this.requestMaxRetries,
                this.innerHttpClientBuilder != null ? this.innerHttpClientBuilder : new InnerApacheHttpClientBuilder(),
                this.validateAfterInactivity);
        }
    }

}
