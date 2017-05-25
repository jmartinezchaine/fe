package uy.com.fusion.library.rest.interceptors.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author jformoso
 * 
 * Stores a response locally to avoid hitting the server on every request. Does not depend on any configuration or
 * response from the server (even Vary headers). Use with caution, intended initially to cache static content that seldom changes.
 */
public class SimpleLocalCacheInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLocalCacheInterceptor.class);

    private Cache<String, CacheElement> cache;
    /**
     * Cache duration in milliseconds
     */
    private long duration;

    public SimpleLocalCacheInterceptor(int size, int concurrencyLevel, long duration) {
        this.cache = CacheBuilder.newBuilder().maximumSize(size).concurrencyLevel(concurrencyLevel).build();
        this.duration = duration;
    }

    public SimpleLocalCacheInterceptor(int size, long duration) {
        this.cache = CacheBuilder.newBuilder().maximumSize(size).build();
        this.duration = duration;
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) { // we don't cache anything else but GET requests
            String key = this.getCacheKey(request);
            if (key != null) {
                CacheElement elem = this.cache.getIfPresent(key);

                if (elem != null) { // cache hit
                    LOGGER.debug("Cache hit for key " + key);
                    long now = System.currentTimeMillis();
                    if (elem.deadline != null && elem.deadline > now) {
                        LOGGER.debug("Last Response is still valid. Returning without sending request to the server");
                        return elem.response.clone();
                    } else {
                        LOGGER.debug("Last Response is no longer valid. Invalidating and forwarding request to the server");
                        this.cache.invalidate(key);
                    }
                } else {
                    LOGGER.debug("Cache miss for key " + key);
                }
            } else {
                LOGGER.debug("Key doesn't exist in cache.");
            }
        }

        return this.next(request);
    }

    @Override
    protected HttpResponseContext postHandle(HttpResponseContext response) throws IOException {

        if (!response.isSuccessfulStreaming() && !response.getStatus().isError()) {
            String cacheKey = this.getCacheKey(((HttpRequestContext) response.getContext("request")));

            CacheElement element = new CacheElement();
            element.response = response.clone();
            element.deadline = System.currentTimeMillis() + this.duration;

            this.cache.put(cacheKey, element);
        }

        return response;
    }

    private String getCacheKey(HttpRequestContext request) {
        return request.getPath();
    }

    private static class CacheElement {
        private HttpResponseContext response;
        private Long deadline;
    }
}
