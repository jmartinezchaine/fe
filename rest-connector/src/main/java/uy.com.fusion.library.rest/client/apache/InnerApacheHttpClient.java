package uy.com.fusion.library.rest.client.apache;

import static uy.com.fusion.library.rest.client.apache.HttpContextMetricsHelper.withMetricsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.HttpHeaders;
import uy.com.fusion.library.rest.HttpStatus;
import uy.com.fusion.library.rest.client.InnerHttpClient;
import uy.com.fusion.library.rest.client.metrics.ExtendedEofSensorInputStream;
import uy.com.fusion.library.rest.client.metrics.MetricRegistry;
import uy.com.fusion.library.rest.client.metrics.ResponseEndTimestampWatcher;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;

public class InnerApacheHttpClient
        extends InnerHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(InnerApacheHttpClient.class);

    private CloseableHttpClient client;
    private RequestConfig requestConfig;

    public InnerApacheHttpClient(CloseableHttpClient client, RequestConfig requestConfig) {
        this.client = client;
        this.requestConfig = requestConfig;
    }

    @Override
    public HttpResponseContext execute(HttpRequestContext requestContext) throws IOException {
        if (requestContext.getBodyParts() != null) {
            throw new RuntimeException("Multipart Body not implemented!");
        }
        MetricRegistry.MetricRegistryBuilder builder = requestContext.getMetricRegistryBuilder().withTimestamp(System.currentTimeMillis())
                .withRequestStartTimestamp(System.nanoTime()).withRequestMethod(requestContext.getMethod().toString())
                .withRequestHost(requestContext.getEndpoint()).withRequestPath(requestContext.getPath())
                .withRequestHeadersLowerCase(requestContext.getHeaders().toLowerCaseMap())
                .withRequestSize(requestContext.getBody() == null ? 0 : requestContext.getBody().length);
        HttpUriRequest request = this.createRequestBuilder(requestContext).build();
        LOGGER.trace("[HTTP-CLIENT] Executing request: " + request.getMethod() + " " + request.getURI());
        try {
            CloseableHttpResponse response = this.client.execute(request,
                    withMetricsBuilder(new BasicHttpContext(), builder));

            HttpStatus status = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
            builder.withResponseStatusCode(status.getCode());

            HttpHeaders responseHeaders = new HttpHeaders();
            for (Header header : response.getAllHeaders()) {
                responseHeaders.set(header.getName(), header.getValue());
            }
            builder.withResponseHeadersLowerCase(responseHeaders.toLowerCaseMap());

            InputStream responseBody;
            if (response.getEntity() != null) {
                responseBody = new ExtendedEofSensorInputStream(response.getEntity().getContent(),
                        new ResponseEndTimestampWatcher(builder));
            } else {
                builder.buildAndDispatch(0, System.nanoTime());
                responseBody = new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY);
            }

            return new HttpResponseContext(status, responseHeaders, responseBody, requestContext.isForStreaming());
        } catch (IOException e) {
            builder.withResponseException(e);
            builder.buildAndDispatch(0, System.nanoTime());
            throw e;
        }
    }


    // //////////////////////////////////////////////
    // APACHE HTTP-REQUEST BUILDER
    // //////////////////////////////////////////////

    private RequestBuilder createRequestBuilder(HttpRequestContext request) {
        RequestBuilder builder = RequestBuilder.create(request.getMethod().name());
        builder.setUri(request.getEndpoint() + request.getPath());

        for (Entry<String, String> header : request.getHeaders().toMap().entrySet()) {
            builder.setHeader(header.getKey(), header.getValue());
        }
        if (request.getHeaders().get("User-Agent") == null) {
            builder.setHeader("User-Agent", "RestConnector/2.X");
        }

        if (request.getPerRequestConfig() != null) {
            Long timeoutInMillis = request.getPerRequestConfig().getTimeoutInMillis();
            if (timeoutInMillis != null && timeoutInMillis > 0) {
                RequestConfig requestConfig = RequestConfig.copy(this.requestConfig)
                        .setSocketTimeout(timeoutInMillis.intValue()).build();
                builder.setConfig(requestConfig);
            }
        }

        if (request.getBody() != null) {
            EntityBuilder eb = EntityBuilder.create().setBinary(request.getBody());
            if ("gzip".equals(request.getHeaders().getContentEncoding())) {
                eb.gzipCompress();
            }
            builder.setEntity(eb.build());
        }

        return builder;
    }

    @Override
    public void shutdown() {
        try {
            this.client.close();
        } catch (IOException e) {
            LOGGER.error("Apache HttpClient could not be closed.", e.getCause());
        }
    }

}
