package uy.com.fusion.tech.rest.connector.client.interceptor;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.HttpStatus;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;

public class ServiceLogInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogInterceptor.class);

    private static final String DEFAULT_BODY_ENCODING = "UTF-8";

    public ServiceLogInterceptor() {
        super();
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        String requestString = request.getBody() != null ? new String(request.getBody(), DEFAULT_BODY_ENCODING) : "null";
        this.logRequest(request.getMethod()
            .name(), request.getEndpoint() + request.getPath(), requestString);
        return this.next(request);
    }

    @Override
    protected HttpResponseContext postHandle(HttpResponseContext response) {
        HttpRequestContext reqContext = (HttpRequestContext) response.getAllContext()
            .get("request");
        if (response.getStatus()
            .getCode() == HttpStatus.OK.getCode()) {
            this.logResponse(reqContext.getEndpoint() + reqContext.getPath(), response);
        } else {

            this.logResponseError(reqContext.getEndpoint() + reqContext.getPath(), response);
        }
        return response;
    }


    private void logRequest(String proto, String context, String request) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} {} >> {}", proto, context, request);
        }
    }

    private void logResponse(String context, HttpResponseContext response) {
        try {
            if (LOGGER.isInfoEnabled()) {
                String responseJson = this.handleResponseBody(response);
                LOGGER.info("{} << {}", context, responseJson);
            }
        } catch (Exception e) {
            LOGGER.warn("Response body from [ " + context + " ] could not be properly handled", e);
        }
    }

    private void logResponseError(String context, HttpResponseContext response) {
        if (LOGGER.isInfoEnabled()) {
            try {
                String responseJson = this.handleResponseBody(response);
                LOGGER.info("{} << Status: {} Body: {}", context, response.getStatus(), responseJson);
            } catch (IOException e) {
                LOGGER.info("{} << Status: {} Body: {}", context, response.getStatus(), " Response body could not be handled ");
            }
        }
    }

    private String handleResponseBody(HttpResponseContext response) throws IOException {
        String encoding = response.getHeaders()
            .getContentEncoding();
        if (encoding == null || encoding.trim()
            .equals("")) {
            encoding = DEFAULT_BODY_ENCODING;
        }
        return IOUtils.toString(response.getBodyInputStream(), encoding);
    }
}
