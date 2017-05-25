package uy.com.fusion.library.rest.interceptors.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;

public class AcceptJsonInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptJsonInterceptor.class);
    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF8 = "UTF-8";

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {

        String acceptHeader = request.getHeaders().getAccept();
        if (acceptHeader == null) {
            LOGGER.debug("Setting header 'Accept: application/json'.");
            request.getHeaders().setAccept(APPLICATION_JSON);
            request.getHeaders().setAcceptCharset(UTF8);
        } else if (!acceptHeader.contains(APPLICATION_JSON)) {
            LOGGER.debug("Adding 'application/json' to 'Accept' header.");
            request.getHeaders().setAccept(acceptHeader + ", " + APPLICATION_JSON);
            request.getHeaders().setAcceptCharset(UTF8);
        }
        return this.next(request);
    }

}
