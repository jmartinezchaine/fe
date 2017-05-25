package uy.com.fusion.library.rest.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import uy.com.fusion.library.rest.RestConnector;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;


/**
 * Utility class to cache rest connectors.
 * Relies on Guava's CacheBuilder, which is an optional dependency, make sure to include it if using
 * this class.
 * @param <K> Type of cache's key. For example, for host:port keys one can use {@link java.lang.String}
 */
public class RestConnectorPool<K> {

    private final AtomicReference<LoadingCache<K, RestConnector>> connectors;

    private RestConnectorPool(final CacheBuilderSpec builderSpec, final CacheLoader<K, RestConnector> cacheLoader,
        final boolean shutdownOnEviction) {
        LoadingCache<K, RestConnector> cache = CacheBuilder.from(builderSpec)
            .removalListener(new RemovalListener<K, RestConnector>() {
                @Override
                public void onRemoval(RemovalNotification<K, RestConnector> notification) {
                    if (shutdownOnEviction) {
                        notification.getValue().shutdown();
                    }
                }
            }).build(cacheLoader);
        this.connectors = new AtomicReference<>(cache);
    }

    /**
     * Default RestConnectorPool uses {@link RestConnector#createWithJsonGzip(hostAndPort)}
     * Cache's maximum size is 20 and evictions happen after 1h of last access
     * @return new instance of default RestConnectorPool&lt;String&gt;
     */
    public static RestConnectorPool<String> createDefaultPool() {
        PoolBuilder<String> builder = createBuilder();
        return builder.cacheLoader(new CacheLoader<String, RestConnector>() {
            @Override
            public RestConnector load(String hostAndPort) throws Exception {
                return RestConnector.createWithJsonGzip(hostAndPort);
            }
        }).build();
    }

    public static <T> PoolBuilder<T> createBuilder() {
        return new PoolBuilder<T>();
    }

    public RestConnector getConnector(K key) throws ExecutionException {
        LoadingCache<K, RestConnector> loadingCache = this.connectors.get();
        if (loadingCache == null) {
            throw new RuntimeException("Pool has already been closed");
        }
        return loadingCache.get(key);
    }

    public void shutdown() {
        LoadingCache<K, RestConnector> connectors = this.connectors.getAndSet(null);
        if (connectors != null) {
            for (RestConnector rc : connectors.asMap().values()) {
                rc.shutdown();
            }
        }
    }

    public static class PoolBuilder<K> {

        private CacheBuilderSpec cacheBuilderSpec = CacheBuilderSpec.parse("maximumSize=20,expireAfterAccess=1h");
        private CacheLoader<K, RestConnector> cacheLoader;
        private boolean shutdownOnEviction = false;

        private PoolBuilder() {
        }

        public PoolBuilder<K> cacheBuilderSpec(CacheBuilderSpec cacheBuilderSpec) {
            this.cacheBuilderSpec = cacheBuilderSpec;
            return this;
        }

        public PoolBuilder<K> cacheLoader(CacheLoader<K, RestConnector> cacheLoader) {
            this.cacheLoader = cacheLoader;
            return this;
        }

        /**
         * WARNING: Be careful when setting this to true. If you keep a reference when getting a
         * connector, it might get shutdown while you are using it if you set a wrong expire policy
         * in the {@link CacheBuilderSpec}
         * @param shutdownOnEviction whether to shutdown restconnector when an entry is evicted
         * @return builder instance
         */
        public PoolBuilder<K> shutdownOnEviction(boolean shutdownOnEviction) {
            this.shutdownOnEviction = shutdownOnEviction;
            return this;
        }

        public RestConnectorPool<K> build() {
            if (this.cacheLoader == null) {
                throw new RuntimeException("Cache loader not set");
            }
            return new RestConnectorPool<K>(this.cacheBuilderSpec, this.cacheLoader, this.shutdownOnEviction);
        }
    }
}
