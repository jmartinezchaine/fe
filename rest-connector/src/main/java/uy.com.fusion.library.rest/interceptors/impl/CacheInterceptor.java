package uy.com.fusion.library.rest.interceptors.impl;

import java.io.IOException;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.HttpMethod;
import uy.com.fusion.library.rest.HttpStatus;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheInterceptor.class);

    public static enum CacheMethods {
        MAX_AGE, EXPIRES, ETAG, LAST_MODIFIED
    }

    private EnumSet<CacheMethods> supportedCacheMethods;
    private Cache<String, CacheElement> cache;
    private Cache<String, String> cachekey;


    public CacheInterceptor(int size, int concurrencyLevel, EnumSet<CacheMethods> supportedCacheMethods) {
        this.cache = CacheBuilder.newBuilder().maximumSize(size).concurrencyLevel(concurrencyLevel).build();
        this.cachekey = CacheBuilder.newBuilder().maximumSize(size).concurrencyLevel(concurrencyLevel).build();
        this.supportedCacheMethods = supportedCacheMethods;
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {

        boolean isGet = request.getMethod() == HttpMethod.GET;

        if (isGet) { // we don't cache anything else but GET requests
            String key = this.getCacheKey(request);
            if (key != null) {
                CacheElement elem = this.cache.getIfPresent(key);

                if (elem != null) { // cache hit
                    LOGGER.debug("Cache hit for key " + key);
                    long now = System.currentTimeMillis();
                    if (elem.deadline != null && elem.deadline > now) {
                        LOGGER.debug("Last Response is still valid. Returning without sending request to client");
                        HttpResponseContext cachedResponse = elem.response.clone();
                        return cachedResponse;
                    } else {
                        if (elem.eTag != null) {
                            LOGGER.debug("Last Response is not valid. Adding ifNoneMatch to headers");
                            request.getHeaders().setIfNoneMatch(elem.eTag);
                            request.setContext("lastResponse", elem.response.clone());
                        }
                        if (elem.lastModified != null) {
                            LOGGER.debug("Last Response is not valid. Adding IfModifiedSince to headers");
                            request.getHeaders().setIfModifiedSince(elem.lastModified);
                            request.setContext("lastResponse", elem.response.clone());
                        }
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

        // if 304 NOT_MODIFIED
        if (response.getStatus().equals(HttpStatus.NOT_MODIFIED)) {
            HttpResponseContext cachedResponse = (HttpResponseContext) response.getContext("lastResponse");
            LOGGER.debug("NOT MODIFED returned. Sending previous response.");

            return cachedResponse != null ? cachedResponse : response;
        } else if (response.getStatus().equals(HttpStatus.OK)) {

            // just fill cache with the response
            long now = System.currentTimeMillis();

            Integer maxAge = this.supports(CacheMethods.MAX_AGE) && response.getHeaders().getMaxAge() > 0
                ? response.getHeaders().getMaxAge() : null;
            Long expires = this.supports(CacheMethods.EXPIRES) && response.getHeaders().getExpires() > now
                ? response.getHeaders().getExpires() : null;
            String eTag = this.supports(CacheMethods.ETAG) ? response.getHeaders().getETag() : null;
            String lastModified = this.supports(CacheMethods.LAST_MODIFIED)
                && response.getHeaders().getLastModified() != null ? response.getHeaders().getLastModified() : null;

            if (maxAge != null || expires != null || eTag != null || lastModified != null) {
                // fill cache
                String cacheKey = this.createCacheKey((HttpRequestContext) response.getContext("request"), response);

                CacheElement element = new CacheElement();
                element.response = response.clone();
                if (maxAge != null) {
                    element.deadline = now + maxAge * 1000;
                } else if (expires != null) {
                    element.deadline = expires;
                }
                element.eTag = eTag;
                element.lastModified = lastModified;

                this.cache.put(cacheKey, element);
            }
        }

        return response;
    }

    private String createCacheKey(HttpRequestContext request, HttpResponseContext response) {

        String varyHeader = response.getHeaders().get("Vary");

        if (varyHeader == null) {
            varyHeader = "Accept, Accept-Encoding";
        }

        this.cachekey.put(request.getPath(), varyHeader);

        return this.getCacheKey(request);
    }

    private String getCacheKey(HttpRequestContext request) {

        String varyHeaders = this.cachekey.getIfPresent(request.getPath());

        if (varyHeaders == null) {
            return null;
        }

        String[] varies = varyHeaders.split(",");

        String path = request.getPath();

        StringBuilder key = new StringBuilder(path);

        for (String v : varies) {
            String header = request.getHeaders().get(v.trim());
            key.append("::").append(header);
        }

        return key.toString();
    }

    private boolean supports(CacheMethods m) {
        return this.supportedCacheMethods.contains(m);
    }

    private static class CacheElement {
        private HttpResponseContext response;
        private Long deadline;
        private String eTag;
        private String lastModified;

    }

}
