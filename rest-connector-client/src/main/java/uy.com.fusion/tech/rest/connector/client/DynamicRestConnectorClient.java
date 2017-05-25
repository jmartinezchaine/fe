package uy.com.fusion.tech.rest.connector.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;

import uy.com.fusion.library.rest.serializers.json.JsonConfig;
import uy.com.fusion.library.rest.utils.TypeReference;
import uy.com.fusion.tech.rest.connector.client.decorator.RequestHeadersDecorator;
import uy.com.fusion.tech.rest.connector.client.logger.ServiceTracker;

public class DynamicRestConnectorClient {
    private static final String DYMANIC_SERVICE_TYPE = "DYNAMIC";
    private static final String SECURE_PROTOCOL = "https";
    private static final JsonConfig.JsonFormat VOID_FORMAT = JsonConfig.JsonFormat.SNAKE_CASE;

    private final Long connectionTimeout;
    private final Long readTimeout;
    private final Long idleConnectionTimeout;
    private final Integer maxConnections;
    private final Long validateAfterInactivity;
    private final String clientId;
    private final SSLContext sslContext;

    private Map<String, RestConnectorClient> clients;

    // Content Type
    private String requestContentType = RestConnectorClient.APPLICATION_JSON;
    private String responseContentType = null;

    // Service Tracking
    private ServiceTracker serviceTracker = null;
    private boolean serviceTrackerEnable = true;

    // Decorator
    private RequestHeadersDecorator requestHeadersDecorator = null;

    // New Relic Logging
    private boolean newRelicErrorLoggingEnable = true;
    private Set<Integer> newRelicIgnoreCodes = new HashSet<>();
    private Set<String> newRelicIgnoreUrls = new HashSet<>();

    public DynamicRestConnectorClient(Long connectionTimeout, Long readTimeout, Long idleConnectionTimeout, Integer maxConnections, String clientId,
                    SSLContext sslContext) {
        this(connectionTimeout, readTimeout, idleConnectionTimeout, maxConnections, RestConnectorClient.DEFAULT_VALIDATE_AFTER_INACTIVITY, clientId,
                        sslContext);
    }

    public DynamicRestConnectorClient(Long connectionTimeout, Long readTimeout, Long idleConnectionTimeout, Integer maxConnections,
                    Long validateAfterInactivity, String clientId, SSLContext sslContext) {
        super();
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.idleConnectionTimeout = idleConnectionTimeout;
        this.maxConnections = maxConnections;
        this.validateAfterInactivity = validateAfterInactivity;
        this.clientId = clientId;
        this.sslContext = sslContext;
        this.clients = new HashMap<>();
    }

    public void shutdown() {
        this.clients.values().stream().forEach(RestConnectorClient::shutdown);
    }

    /*** GET ***/

    public <R> R doGet(JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doGet(serviceUrl, typeReference, uriParams);
    }

    public <R> R doGet(JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams)
                    throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doGet(serviceUrl, typeReference, headers, uriParams);
    }

    public <R> R doGet(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doGet(trackingDataBuilder, serviceUrl, typeReference, uriParams);
    }

    public <R> R doGet(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference,
                    Map<String, String> headers, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doGet(trackingDataBuilder, serviceUrl, typeReference, headers, uriParams);
    }

    /*** POST ***/

    public void doPost(String url, Object body, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doPost(serviceUrl, body, uriParams);
    }

    public <R> R doPost(JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference, String... uriParams)
                    throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPost(serviceUrl, body, typeReference, uriParams);
    }

    public <R> R doPost(JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPost(serviceUrl, body, typeReference, headers, uriParams);
    }

    public void doPost(TrackingDataBuilder trackingDataBuilder, String url, Object body, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doPost(trackingDataBuilder, serviceUrl, body, uriParams);
    }

    public <R> R doPost(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPost(trackingDataBuilder, serviceUrl, body, typeReference, uriParams);
    }

    public <R> R doPost(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference,
                    Map<String, String> headers, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPost(trackingDataBuilder, serviceUrl, body, typeReference, headers, uriParams);
    }

    /*** PUT ***/

    public void doPut(String url, Object body, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doPut(serviceUrl, body, uriParams);
    }

    public <R> R doPut(JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference, String... uriParams)
                    throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPut(serviceUrl, body, typeReference, uriParams);
    }

    public <R> R doPut(JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPut(serviceUrl, body, typeReference, headers, uriParams);
    }

    public void doPut(TrackingDataBuilder trackingDataBuilder, String url, Object body, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doPut(trackingDataBuilder, serviceUrl, body, uriParams);
    }

    public <R> R doPut(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPut(trackingDataBuilder, serviceUrl, body, typeReference, uriParams);
    }

    public <R> R doPut(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference,
                    Map<String, String> headers, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPut(trackingDataBuilder, serviceUrl, body, typeReference, headers, uriParams);
    }

    /*** PATCH ***/

    public void doPatch(String url, Object body, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doPatch(serviceUrl, body, uriParams);
    }

    public <R> R doPatch(JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference, String... uriParams)
                    throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPatch(serviceUrl, body, typeReference, uriParams);
    }

    public <R> R doPatch(JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference, Map<String, String> headers,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPatch(serviceUrl, body, typeReference, headers, uriParams);
    }

    public void doPatch(TrackingDataBuilder trackingDataBuilder, String url, Object body, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doPatch(trackingDataBuilder, serviceUrl, body, uriParams);
    }

    public <R> R doPatch(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPatch(trackingDataBuilder, serviceUrl, body, typeReference, uriParams);
    }

    public <R> R doPatch(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, Object body, TypeReference<R> typeReference,
                    Map<String, String> headers, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doPatch(trackingDataBuilder, serviceUrl, body, typeReference, headers, uriParams);
    }

    /*** DELETE ***/

    public void doDelete(String url, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doDelete(serviceUrl, uriParams);
    }

    public <R> R doDelete(JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doDelete(serviceUrl, typeReference, uriParams);
    }

    public <R> R doDelete(JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference, Map<String, String> headers, String... uriParams)
                    throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doDelete(serviceUrl, typeReference, headers, uriParams);
    }

    public void doDelete(TrackingDataBuilder trackingDataBuilder, String url, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, VOID_FORMAT);
        String serviceUrl = aURL.getFile();
        restConnectorClient.doDelete(trackingDataBuilder, serviceUrl, uriParams);
    }

    public <R> R doDelete(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference,
                    String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doDelete(trackingDataBuilder, serviceUrl, typeReference, uriParams);
    }

    public <R> R doDelete(TrackingDataBuilder trackingDataBuilder, JsonConfig.JsonFormat jsonFormat, String url, TypeReference<R> typeReference,
                    Map<String, String> headers, String... uriParams) throws MalformedURLException {
        URL aURL = new URL(url);
        RestConnectorClient restConnectorClient = this.getRestConnectorClient(aURL, jsonFormat);
        String serviceUrl = aURL.getFile();
        return restConnectorClient.doDelete(trackingDataBuilder, serviceUrl, typeReference, headers, uriParams);
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

    public void setRequestHeadersDecorator(RequestHeadersDecorator decorator) {
        this.requestHeadersDecorator = decorator;
    }

    /****** Auxiliary Methods *******/

    private RestConnectorClient getRestConnectorClient(URL aURL, JsonConfig.JsonFormat jsonFormat) {
        String protocol = aURL.getProtocol();
        String host = aURL.getAuthority();

        String key = this.calculateKey(jsonFormat, protocol, host);
        RestConnectorClient restConnectorClient = this.clients.get(key);
        if (restConnectorClient == null) {
            restConnectorClient = this.addRestConnectorClient(jsonFormat, protocol, host);
        }
        return restConnectorClient;
    }

    private String calculateKey(JsonConfig.JsonFormat jsonFormat, String protocol, String host) {
        return jsonFormat + "-" + protocol + "-" + host;
    }

    private synchronized RestConnectorClient addRestConnectorClient(JsonConfig.JsonFormat jsonFormat, String protocol, String host) {
        SSLContext context = null;
        if (SECURE_PROTOCOL.equals(protocol)) {
            context = this.sslContext;
        }

        RestConnectorClient restConnectorClient = new RestConnectorClient(protocol, host, StringUtils.EMPTY, jsonFormat, this.connectionTimeout,
                        this.readTimeout, this.idleConnectionTimeout, this.maxConnections, this.validateAfterInactivity, this.clientId, context) {

            @Override
            protected String getServiceType() {
                return DYMANIC_SERVICE_TYPE;
            }
        };

        // Set Optional Properties
        restConnectorClient.setRequestContentType(this.requestContentType);
        restConnectorClient.setResponseContentType(this.responseContentType);
        restConnectorClient.setServiceTracker(this.serviceTracker);
        restConnectorClient.setServiceTrackerEnable(this.serviceTrackerEnable);
        restConnectorClient.setNewRelicErrorLoggingEnable(this.newRelicErrorLoggingEnable);
        restConnectorClient.addNewRelicIgnoreCodes(this.newRelicIgnoreUrls.toArray(new Integer[0]));
        restConnectorClient.addNewRelicIgnoreUrls(this.newRelicIgnoreUrls.toArray(new String[0]));

        String key = this.calculateKey(jsonFormat, protocol, host);
        this.clients.put(key, restConnectorClient);

        return restConnectorClient;
    }
}
