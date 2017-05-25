package uy.com.fusion.tech.rest.connector.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.StopWatch;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newrelic.api.agent.NewRelic;

import uy.com.fusion.library.rest.HttpResponse;
import uy.com.fusion.library.rest.HttpStatus;
import uy.com.fusion.library.rest.RestConnector;
import uy.com.fusion.library.rest.builder.HttpRequestBuilder;
import uy.com.fusion.library.rest.config.RestConnectorConfig;
import uy.com.fusion.library.rest.serializers.json.JsonConfig;
import uy.com.fusion.library.rest.utils.Assert;
import uy.com.fusion.library.rest.utils.TypeReference;
import uy.com.fusion.tech.rest.connector.client.exception.HttpRestConnectorClientException;
import uy.com.fusion.tech.rest.connector.client.exception.RestConnectorClientException;
import uy.com.fusion.tech.rest.connector.client.logger.ServiceTracker;
import uy.com.fusion.tech.rest.connector.client.newrelic.NewRelicDataHolder;
import uy.com.fusion.tech.rest.connector.client.newrelic.NewRelicReporter;
import uy.com.fusion.tech.rest.connector.client.util.TrackingDataBuilderHelper;

public abstract class RestConnectorClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestConnectorClient.class);


    public enum RequestType {
        PUT, DELETE, GET, POST, PATCH, HEAD
    }


    protected static final Long DEFAULT_VALIDATE_AFTER_INACTIVITY = 2000L;
    protected static final String APPLICATION_HESSIAN = "application/x-hessian";
    protected static final String APPLICATION_JSON = "application/json; charset=utf-8";
    protected static final String PLAIN_TEXT = "text/plain";
    protected static final String DEFAULT_PROTOCOL = "http";

    protected static final String ERROR_DESCRIPTION = "Description";
    protected static final String ERROR_APP = "APP";
    protected static final String URI_PARAM_IDENTIFIER = "{}";

    private final RestConnector restConnector;
    private final String contextPath;

    private String requestContentType = APPLICATION_JSON;
    private String responseContentType = null;

    // Service Tracking
    private ServiceTracker serviceTracker;
    private boolean serviceTrackerEnable = true;

    // New Relic Logging
    private boolean newRelicErrorLoggingEnable = true;
    private Set<Integer> newRelicIgnoreCodes = new HashSet<>();
    private Set<String> newRelicIgnoreUrls = new HashSet<>();

    // Log
    private boolean headersLogEnable = true;

    public RestConnectorClient() {
        LOGGER.info("Build EMPTY RestConnectorClient. type={}", this.getServiceType());

        this.restConnector = null;
        this.contextPath = null;
    }

    public RestConnectorClient(RestConnector restConnector, String contextPath) {
        LOGGER.info("Build RestConnectorClient by RestConnector. type={}", this.getServiceType());

        this.restConnector = restConnector;
        this.contextPath = contextPath != null ? contextPath : "";
    }

    public RestConnectorClient(String host, JsonConfig.JsonFormat jsonFormat, Long connectionTimeout, Long readTimeout, Long idleConnectionTimeout,
                    Integer maxConnections, String clientId) {
        this(DEFAULT_PROTOCOL, host, null, jsonFormat, null, connectionTimeout, readTimeout, idleConnectionTimeout, maxConnections,
                        DEFAULT_VALIDATE_AFTER_INACTIVITY, clientId, null);
    }

    public RestConnectorClient(String host, String contextPath, JsonConfig.JsonFormat jsonFormat, Long connectionTimeout, Long readTimeout,
                    Long idleConnectionTimeout, Integer maxConnections, String clientId) {
        this(DEFAULT_PROTOCOL, host, contextPath, jsonFormat, null, connectionTimeout, readTimeout, idleConnectionTimeout, maxConnections,
                        DEFAULT_VALIDATE_AFTER_INACTIVITY, clientId, null);
    }

    public RestConnectorClient(String protocol, String host, String contextPath, JsonConfig.JsonFormat jsonFormat, Long connectionTimeout, Long readTimeout,
                    Long idleConnectionTimeout, Integer maxConnections, String clientId, SSLContext sslContext) {
        this(protocol, host, contextPath, jsonFormat, null, connectionTimeout, readTimeout, idleConnectionTimeout, maxConnections,
                        DEFAULT_VALIDATE_AFTER_INACTIVITY, clientId, sslContext);
    }

    public RestConnectorClient(String protocol, String host, String contextPath, JsonConfig.JsonFormat jsonFormat, Long connectionTimeout, Long readTimeout,
                    Long idleConnectionTimeout, Integer maxConnections, Long validateAfterInactivity, String clientId, SSLContext sslContext) {
        this(protocol, host, contextPath, jsonFormat, null, connectionTimeout, readTimeout, idleConnectionTimeout, maxConnections, validateAfterInactivity,
                        clientId, sslContext);
    }

    public RestConnectorClient(String protocol, String host, String contextPath, JsonConfig.JsonFormat jsonFormat, DateTimeFormatter dateTimeFormatter,
                    Long connectionTimeout, Long readTimeout, Long idleConnectionTimeout, Integer maxConnections, Long validateAfterInactivity, String clientId,
                    SSLContext sslContext) {

        LOGGER.info("Build RestConnectorClient. type={}, protocol={}, host={}, jsonFormat={}", this.getServiceType(), protocol, host, jsonFormat);

        this.contextPath = contextPath != null ? contextPath : "";
        this.restConnector = buildRestConnector(protocol, host, jsonFormat, dateTimeFormatter, connectionTimeout, readTimeout, idleConnectionTimeout,
                        maxConnections, validateAfterInactivity, clientId, sslContext);
    }

    public void shutdown() {
        if (this.restConnector != null) {
            this.restConnector.shutdown();
        }
        NewRelicReporter.shutdown();
    }

    /*** GET ***/

    public <R> R doGet(String serviceUrl, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, null, typeReference, RequestType.GET, null, uriParams);
    }

    public <R> R doGet(String serviceUrl, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, null, typeReference, RequestType.GET, headers, uriParams);
    }

    public <R> R doGet(TrackingDataBuilder trackingDataBuilder, String serviceUrl, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, null, typeReference, RequestType.GET, null, uriParams);
    }

    public <R> R doGet(TrackingDataBuilder trackingDataBuilder, String serviceUrl, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, null, typeReference, RequestType.GET, headers, uriParams);
    }

    /*** POST ***/

    public void doPost(String serviceUrl, Object body, String... uriParams) {
        this.execute(new TrackingDataBuilder(), serviceUrl, body, null, RequestType.POST, null, uriParams);
    }

    public <R> R doPost(String serviceUrl, Object body, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, body, typeReference, RequestType.POST, null, uriParams);
    }

    public <R> R doPost(String serviceUrl, Object body, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, body, typeReference, RequestType.POST, headers, uriParams);
    }

    public void doPost(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, String... uriParams) {
        this.execute(trackingDataBuilder, serviceUrl, body, null, RequestType.POST, null, uriParams);
    }

    public <R> R doPost(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, body, typeReference, RequestType.POST, null, uriParams);
    }

    public <R> R doPost(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, body, typeReference, RequestType.POST, headers, uriParams);
    }

    /*** PUT ***/

    public void doPut(String serviceUrl, Object body, String... uriParams) {
        this.execute(new TrackingDataBuilder(), serviceUrl, body, null, RequestType.PUT, null, uriParams);
    }

    public <R> R doPut(String serviceUrl, Object body, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, body, typeReference, RequestType.PUT, null, uriParams);
    }

    public <R> R doPut(String serviceUrl, Object body, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, body, typeReference, RequestType.PUT, headers, uriParams);
    }

    public void doPut(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, String... uriParams) {
        this.execute(trackingDataBuilder, serviceUrl, body, null, RequestType.PUT, null, uriParams);
    }

    public <R> R doPut(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, body, typeReference, RequestType.PUT, null, uriParams);
    }

    public <R> R doPut(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, body, typeReference, RequestType.PUT, headers, uriParams);
    }

    /*** PATCH ***/

    public void doPatch(String serviceUrl, Object body, String... uriParams) {
        this.execute(new TrackingDataBuilder(), serviceUrl, body, null, RequestType.PATCH, null, uriParams);
    }

    public <R> R doPatch(String serviceUrl, Object body, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, body, typeReference, RequestType.PATCH, null, uriParams);
    }

    public <R> R doPatch(String serviceUrl, Object body, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, body, typeReference, RequestType.PATCH, headers, uriParams);
    }

    public void doPatch(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, String... uriParams) {
        this.execute(trackingDataBuilder, serviceUrl, body, null, RequestType.PATCH, null, uriParams);
    }

    public <R> R doPatch(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, body, typeReference, RequestType.PATCH, null, uriParams);
    }

    public <R> R doPatch(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object body, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, body, typeReference, RequestType.PATCH, headers, uriParams);
    }

    /*** DELETE ***/

    public void doDelete(String serviceUrl, String... uriParams) {
        this.execute(new TrackingDataBuilder(), serviceUrl, null, null, RequestType.DELETE, null, uriParams);
    }

    public <R> R doDelete(String serviceUrl, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, null, typeReference, RequestType.DELETE, null, uriParams);
    }

    public <R> R doDelete(String serviceUrl, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, null, typeReference, RequestType.DELETE, headers, uriParams);
    }

    public void doDelete(TrackingDataBuilder trackingDataBuilder, String serviceUrl, String... uriParams) {
        this.execute(trackingDataBuilder, serviceUrl, null, null, RequestType.DELETE, null, uriParams);
    }

    public <R> R doDelete(TrackingDataBuilder trackingDataBuilder, String serviceUrl, TypeReference<R> typeReference, String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, null, typeReference, RequestType.DELETE, null, uriParams);
    }

    public <R> R doDelete(TrackingDataBuilder trackingDataBuilder, String serviceUrl, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) {
        return this.execute(trackingDataBuilder, serviceUrl, null, typeReference, RequestType.DELETE, headers, uriParams);
    }

    /*** HEAD ***/

    public Map<String, String> doHead(String serviceUrl, String... uriParams) {
        return this.execute(new TrackingDataBuilder(), serviceUrl, null, new TypeReference<Map<String, String>>() {
        }, RequestType.HEAD, null, uriParams);
    }

    /*** Optional Setters***/

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public void setServiceTracker(ServiceTracker serviceTracker) {
        this.serviceTracker = serviceTracker;
    }


    public void setServiceTrackerEnable(boolean serviceTrackerEnable) {
        this.serviceTrackerEnable = serviceTrackerEnable;
    }

    public void setNewRelicErrorLoggingEnable(boolean newRelicErrorLoggingEnable) {
        this.newRelicErrorLoggingEnable = newRelicErrorLoggingEnable;
    }

    public void addNewRelicIgnoreCodes(Integer... codes) {
        this.newRelicIgnoreCodes.addAll(Arrays.asList(codes));
    }

    public void addNewRelicIgnoreUrls(String... urls) {
        this.newRelicIgnoreUrls.addAll(Arrays.asList(urls));
    }

    public RestConnector getRestConnector() {
        return this.restConnector;
    }

    public void setHeadersLogEnable(boolean headersLogEnable) {
        this.headersLogEnable = headersLogEnable;
    }

    /*** Auxiliary Builders ***/

    protected static final RestConnector buildRestConnector(String protocol, String host, JsonConfig.JsonFormat jsonFormat, DateTimeFormatter dateTimeFormatter,
                    Long connectionTimeout, Long readTimeout, Long idleConnectionTimeout, Integer maxConnections, Long validateAfterInactivity, String clientId,
                    SSLContext sslContext) {

        RestConnector restConnector = null;
        JsonConfig jsonConfig = buildJsonConfig(jsonFormat, dateTimeFormatter);

        RestConnectorConfig restConnectorConfig = RestConnectorConfig.createBuilder()
                        .connectionTimeout(connectionTimeout)
                        .readTimeout(readTimeout)
                        .maxConnections(maxConnections)
                        .idleConnectionTimeout(idleConnectionTimeout)
                        .withValidateAfterInactivity(validateAfterInactivity)
                        .jsonConfig(jsonConfig)
                        .build();
        if (sslContext == null) {
            restConnector = RestConnector.createfusionRestConnector(protocol, host, restConnectorConfig);
        } else {
            restConnector = RestConnector.createfusionRestConnector(protocol, host, restConnectorConfig, sslContext);
        }
        return restConnector;
    }

    protected static final JsonConfig buildJsonConfig(JsonConfig.JsonFormat jsonFormat, DateTimeFormatter dateTimeFormatter) {
        JsonConfig.ConfigBuilder jsonBuilder = JsonConfig.createBuilder();

        if (JsonConfig.JsonFormat.SNAKE_CASE.equals(jsonFormat)) {
            jsonBuilder.withSnakeCaseFormat();
        } else {
            jsonBuilder.withCamelCaseFormat();
        }

        if (dateTimeFormatter != null) {
            jsonBuilder.withDateTimeFormatter(dateTimeFormatter);
        }

        return jsonBuilder.build();
    }

    /*** Auxiliary Methods ***/

    private boolean isServiceTrakingEnable() {
        return this.serviceTrackerEnable && this.serviceTracker != null;
    }

    private boolean isServiceTrakingEnable(TrackingDataBuilder trakingDataBuilder) {
        return this.isServiceTrakingEnable() && trakingDataBuilder.isTrackingEnable();
    }

    private boolean isNewRelicLoggingEnable(String url, Integer code) {
        return this.newRelicErrorLoggingEnable && (code == null || !this.newRelicIgnoreCodes.contains(code)) && (url == null
                        || !this.newRelicIgnoreUrls.contains(url));
    }

    private <R> R execute(TrackingDataBuilder trackingDataBuilder, String serviceUrl, Object requestBody, TypeReference<R> typeReference, RequestType type,
                    Map<String, String> headers, String[] uriParams) {

        NewRelicDataHolder newRelicDataHolder = new NewRelicDataHolder();
        String fullUrl = this.buildUrl(serviceUrl, uriParams);
        String serviceType = this.getServiceType();
        Date serviceDate = new Date();
        final StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        try {
            NewRelicReporter.appendNRCommonParams(newRelicDataHolder, this.contextPath + serviceUrl, serviceType, this.microbalanced(),
                            this.isServiceTrakingEnable(trackingDataBuilder), type, headers, uriParams);

            TrackingDataBuilderHelper.set(trackingDataBuilder);
            trackingDataBuilder.endpoint(this.restConnector.getEndpoint()).serviceUrl(fullUrl).requestType(type).input(requestBody).uriParams(uriParams);

            HttpResponse response = null;

            // Excecute
            try {
                HttpRequestBuilder requestBuilder = this.extractBuilder(fullUrl, requestBody, type, headers);
                response = requestBuilder.execute();
            } catch (Exception ex) {
                String message = serviceType + " - RestConnector execution error";
                this.noticeError(newRelicDataHolder, trackingDataBuilder, message, serviceUrl, headers, uriParams, ex);
                throw new RestConnectorClientException(message, ex);
            }

            // Process
            try {
                trackingDataBuilder.httpStatus(response.getStatus());
                this.validateResponseStatus(newRelicDataHolder, trackingDataBuilder, response, serviceUrl, headers, uriParams);

                R responseBody = this.processResponse(type, response, typeReference);

                // Track it!
                trackingDataBuilder.output(responseBody).custom(requestBody, responseBody);

                return responseBody;

            } catch (IOException ex) {
                String message = serviceType + " - Deserialization error";
                this.noticeError(newRelicDataHolder, trackingDataBuilder, message, serviceUrl, headers, uriParams, ex);
                throw new RestConnectorClientException(message, ex);
            }

        } finally {
            trackingDataBuilder.serviceTime(stopwatch.toString());

            long serviceTime = stopwatch.getTime();

            this.serviceTrackerLog(fullUrl, serviceType, serviceDate, trackingDataBuilder);

            TrackingDataBuilderHelper.clear();
            LOGGER.info("{}: Time={}ms", fullUrl, serviceTime);

        }
    }

    private void noticeError(NewRelicDataHolder newRelicDataHolder, TrackingDataBuilder trackingDataBuilder, String message, String url,
                    Map<String, String> headers, String[] uriParams, Exception ex) {
        NewRelicReporter.appendNRTrace(newRelicDataHolder, ex);
        this.logFailure(url, ex);

        trackingDataBuilder.error(ex.getLocalizedMessage()).trace(ex.getStackTrace());

        if (this.isNewRelicLoggingEnable(url, null)) {
            NewRelic.noticeError(message);
        }
    }

    private String buildUrl(String serviceUrl, Object[] uriParams) {
        String urlSuffix = null;
        if (ArrayUtils.isNotEmpty(uriParams)) {
            String partialUrl = serviceUrl.replaceAll("\\{\\}", "%s");
            urlSuffix = String.format(partialUrl, uriParams);
        } else {
            urlSuffix = serviceUrl;
        }
        return this.contextPath + urlSuffix;
    }

    private void serviceTrackerLog(String fullUrl, String serviceType, Date serviceDate, TrackingDataBuilder trakingDataBuilder) {
        if (this.isServiceTrakingEnable(trakingDataBuilder)) {
            Map<String, Object> trackingDebug = trakingDataBuilder.trackingDebug();
            Map<String, Object> trackingData = trakingDataBuilder.trackingData();

            String trackingDescription = trakingDataBuilder.trackingDescription();
            if (trackingDescription == null) {
                trackingDescription = fullUrl;
            }

            this.serviceTracker.log(serviceDate, trackingDescription, serviceType, trackingDebug, trackingData);
        }
    }

    @SuppressWarnings("unchecked")
    private <R> R processResponse(RequestType type, HttpResponse response, TypeReference<R> typeReference) throws IOException {
        if (typeReference == null) {
            response.getBodyAsInputStream().close();
            return null;
        }

        if (type.equals(RequestType.HEAD)) {
            // En el caso de HEAD, R siempre es Mas<String,String>
            return (R) response.getHeaders().toMap();
        }

        // Handle Content Type
        if (typeReference.getType().equals(String.class)) {
            response.getHeaders().setContentType(PLAIN_TEXT);
        } else if (this.responseContentType != null) {
            response.getHeaders().setContentType(this.responseContentType);
        }

        return response.getBodyAs(typeReference);
    }

    private void validateResponseStatus(NewRelicDataHolder newRelicDataHolder, TrackingDataBuilder trackingDataBuilder, HttpResponse response, String url,
                    Map<String, String> headers, String[] uriParams) {
        HttpStatus status = response.getStatus();

        if (!HttpStatus.OK.getSerie().equals(status.getSerie())) {
            HttpRestConnectorClientException httpException = new HttpRestConnectorClientException(status, response.getBodyAsByteArray());

            String responseBodyAsString = httpException.getResponseBodyAsString();
            trackingDataBuilder.output(responseBodyAsString);

            NewRelicReporter.appendNRTrace(newRelicDataHolder, httpException);
            NewRelicReporter.appendNRBody(responseBodyAsString);
            LOGGER.warn("{} <- {} BODY: {}", status, url, responseBodyAsString);

            if (this.isNewRelicLoggingEnable(url, status.getCode())) {
                NewRelic.noticeError(status + " : " + url);
            }

            throw httpException;
        }
    }

    private HttpRequestBuilder extractBuilder(String url, Object body, RequestType type, Map<String, String> headers)
                    throws UnsupportedEncodingException, URISyntaxException {

        Assert.notNull(this.restConnector, "RestConnector is NULL!");
        HttpRequestBuilder requestBuilder = null;

        switch (type) {
        case DELETE:
            requestBuilder = this.restConnector.delete(url);
            break;
        case GET:
            requestBuilder = this.restConnector.get(url);
            break;
        case PUT:
            requestBuilder = this.restConnector.put(url);
            break;
        case POST:
            requestBuilder = this.restConnector.post(url);
            break;
        case PATCH:
            requestBuilder = this.restConnector.patch(url);
            break;
        case HEAD:
            requestBuilder = this.restConnector.head(url);
            break;
        default:
            throw new IllegalArgumentException("RequestType " + type + " is not supported");
        }

        if (body != null) {
            requestBuilder = requestBuilder.withBody(body).asContentType(this.requestContentType);
        }
        if (MapUtils.isNotEmpty(headers)) {
            for (Entry<String, String> header : headers.entrySet()) {
                requestBuilder = requestBuilder.withHeader(header.getKey(), header.getValue());
            }
        }
        return requestBuilder;
    }

    /*** Overridable Methods ***/

    protected void logFailure(String context, Throwable error) {
        LOGGER.warn("{} << {}", context, error.getMessage());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(error.toString(), error);
        }
    }

    protected boolean microbalanced() {
        return false;
    }

    protected abstract String getServiceType();
}
