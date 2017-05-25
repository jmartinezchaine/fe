package uy.com.fusion.library.rest.interceptors.impl;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.SnappyInputStream;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;

public class SnappyResponseBodyInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnappyResponseBodyInterceptor.class);
    private boolean enforceSnappy;

    public SnappyResponseBodyInterceptor() {
        this(false);
    }

    public SnappyResponseBodyInterceptor(boolean enforceSnappy) {
        this.enforceSnappy = enforceSnappy;
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        if (this.enforceSnappy) {
            LOGGER.debug("Setting header Accept-Encoding: x-snappy");
            String encoding = request.getHeaders().getAcceptEncoding();
            if (encoding == null || encoding.isEmpty()) {
                request.getHeaders().setAcceptEncoding("x-snappy");
            } else {
                if (!encoding.contains("x-snappy")) {
                    request.getHeaders().setAcceptEncoding(encoding + ", x-snappy");
                }
            }
        }
        return this.next(request);
    }


    @Override
    protected HttpResponseContext postHandle(HttpResponseContext response) throws IOException {

        try {
            String encoding = response.getHeaders().getContentEncoding();

            if (encoding != null && encoding.equalsIgnoreCase("x-snappy")) {
                LOGGER.debug("Decompressing http response with SNAPPY");

                InputStream in = response.getBodyInputStream();
                response.setBodyStream(new SnappyInputStream(in));
            }
            return response;
        } catch (IOException e) {
            LOGGER.debug("Error: Error while creating Gzip Input Stream", e);
            throw new RuntimeException("Error while creating Gzip Input Stream", e);
        }
    }

}
