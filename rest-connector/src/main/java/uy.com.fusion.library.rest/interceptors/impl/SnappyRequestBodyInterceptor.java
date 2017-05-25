package uy.com.fusion.library.rest.interceptors.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;
import uy.com.fusion.library.rest.utils.DependencyUtils;

public class SnappyRequestBodyInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnappyRequestBodyInterceptor.class);
    private static boolean SNAPPY_AVAILABLE = DependencyUtils.isClassPresent("org.xerial.snappy.Snappy");

    private boolean enforceSnappy;

    public SnappyRequestBodyInterceptor() {
        this(false);
    }

    public SnappyRequestBodyInterceptor(boolean enforceSnappy) {
        this.enforceSnappy = enforceSnappy;
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        if (SNAPPY_AVAILABLE && request.getMethod().acceptsBody() && request.getBody() != null) {
            String contentEncoding = request.getHeaders().getContentEncoding();
            if ((contentEncoding != null && "x-snappy".equalsIgnoreCase(contentEncoding))
                || (this.enforceSnappy && (contentEncoding == null || "x-snappy".equalsIgnoreCase(contentEncoding)))) {

                LOGGER.debug("Compressing http request body with SNAPPY");

                request.getHeaders().setContentEncoding("x-snappy");

                byte[] body = request.getBody();

                byte[] compressedBody = Snappy.compress(body);

                request.setBody(compressedBody);
            }
        }

        return this.next(request);
    }

}
