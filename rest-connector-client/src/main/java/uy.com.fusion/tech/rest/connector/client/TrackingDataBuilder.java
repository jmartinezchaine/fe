package uy.com.fusion.tech.rest.connector.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import uy.com.fusion.library.rest.HttpStatus;


public class TrackingDataBuilder {
    public static final String KT_TRACE = "trace";
    public static final String KT_ERROR = "error";
    public static final String KT_SERVICE_TIME = "service_time";
    public static final String KT_OUTPUT = "output";
    public static final String KT_INPUT = "input";
    public static final String KT_ENDPOINT = "endpoint";
    public static final String KT_REQUEST_TYPE = "request_type";
    public static final String KT_HTTP_STATUS = "http_status";
    public static final String KT_SERVICE_URL = "service_url";
    public static final String KT_HEADERS = "headers";
    public static final String KT_HEADERS_DECORATED = "headers-decorated";
    public static final String KT_URI_PARAMS = "uri_params";
    public static final String KT_CUSTOM = "custom";

    private Map<String, Object> trackingData;
    private Map<String, Object> trackingDebug;
    private List<String> dataKeyTypes;
    private List<String> debugKeyTypes;

    @SuppressWarnings("rawtypes")
    private CustomDataBuilder customDataBuilder;
    private String trackingDescription;

    private boolean trackingEnable;

    @SuppressWarnings("rawtypes")
    public TrackingDataBuilder(CustomDataBuilder customDataBuilder, String trackingDescription) {
        this();
        this.customDataBuilder = customDataBuilder;
        this.trackingDescription = trackingDescription;
    }

    @SuppressWarnings("rawtypes")
    public TrackingDataBuilder(CustomDataBuilder customDataBuilder) {
        this();
        this.customDataBuilder = customDataBuilder;
    }

    public TrackingDataBuilder(String trackingDescription) {
        this();
        this.trackingDescription = trackingDescription;
    }

    public TrackingDataBuilder() {
        this.trackingData = new HashMap<>();
        this.trackingDebug = new HashMap<>();

        this.dataKeyTypes = new ArrayList<>();
        Collections.addAll(this.dataKeyTypes, KT_ERROR);

        this.debugKeyTypes = new ArrayList<>();
        Collections.addAll(this.debugKeyTypes, KT_TRACE, KT_SERVICE_TIME, KT_OUTPUT, KT_INPUT, KT_REQUEST_TYPE, KT_HTTP_STATUS, KT_SERVICE_URL, KT_HEADERS,
                        KT_HEADERS_DECORATED, KT_URI_PARAMS, KT_CUSTOM, KT_ENDPOINT);

        this.trackingEnable = true;
    }

    public TrackingDataBuilder(List<String> dataKeyTypes, List<String> debugKeyTypes) {
        this.trackingData = new HashMap<>();
        this.trackingDebug = new HashMap<>();
        this.dataKeyTypes = dataKeyTypes;
        this.debugKeyTypes = debugKeyTypes;
    }

    public TrackingDataBuilder serviceUrl(String serviceUrl) {
        this.track(KT_SERVICE_URL, serviceUrl);
        return this;
    }

    public TrackingDataBuilder requestType(RestConnectorClient.RequestType requestType) {
        this.track(KT_REQUEST_TYPE, requestType);
        return this;
    }

    public TrackingDataBuilder input(Object input) {
        this.track(KT_INPUT, input);
        return this;
    }

    public TrackingDataBuilder output(Object output) {
        this.track(KT_OUTPUT, output);
        return this;
    }

    public TrackingDataBuilder serviceTime(String serviceTime) {
        this.track(KT_SERVICE_TIME, serviceTime);
        return this;
    }

    public TrackingDataBuilder error(String error) {
        this.track(KT_ERROR, error);
        return this;
    }

    public TrackingDataBuilder endpoint(String endpoint) {
        this.track(KT_ENDPOINT, endpoint);
        return this;
    }

    public TrackingDataBuilder trace(StackTraceElement[] trace) {
        this.track(KT_TRACE, trace);
        return this;
    }

    public TrackingDataBuilder httpStatus(HttpStatus status) {
        this.track(KT_HTTP_STATUS, status.toString());
        return this;
    }

    public TrackingDataBuilder uriParams(String[] uriParams) {
        if (ArrayUtils.isNotEmpty(uriParams)) {
            this.track(KT_URI_PARAMS, uriParams);
        }
        return this;
    }

    public TrackingDataBuilder headers(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.track(KT_HEADERS, headers);
        }
        return this;
    }

    public TrackingDataBuilder headersDecorated(Map<String, Object> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.track(KT_HEADERS_DECORATED, headers);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public TrackingDataBuilder custom(Object requestBody, Object responseBody) {
        if (this.customDataBuilder != null) {
            Object custom = this.customDataBuilder.build(requestBody, responseBody);
            this.track(KT_CUSTOM, custom);
        }
        return this;
    }

    public TrackingDataBuilder trackingDescription(String trackingDescription) {
        this.trackingDescription = trackingDescription;
        return this;
    }

    // Track Config

    public TrackingDataBuilder dataKeyTypes(List<String> dataKeyTypes) {
        this.dataKeyTypes = dataKeyTypes;
        return this;
    }

    public TrackingDataBuilder debugKeyTypes(List<String> debugKeyTypes) {
        this.debugKeyTypes = debugKeyTypes;
        return this;
    }

    public TrackingDataBuilder trackingEnable(boolean trackingEnable) {
        this.trackingEnable = trackingEnable;
        return this;
    }

    // Getters

    public Map<String, Object> trackingData() {
        if (!this.trackingData.isEmpty()) {
            return this.trackingData;
        }
        return null;
    }

    public Map<String, Object> trackingDebug() {
        if (!this.trackingDebug.isEmpty()) {
            return this.trackingDebug;
        }
        return null;
    }

    public List<String> dataKeyTypes() {
        return this.dataKeyTypes;
    }

    public List<String> debugKeyTypes() {
        return this.debugKeyTypes;
    }

    public String trackingDescription() {
        return this.trackingDescription;
    }

    public boolean isTrackingEnable() {
        return this.trackingEnable;
    }

    // Auxiliary

    private void track(String keyType, Object obj) {
        if (obj != null) {
            if (this.dataKeyTypes != null && this.dataKeyTypes.contains(keyType)) {
                this.trackingData.put(keyType, obj);
            }
            if (this.debugKeyTypes != null && this.debugKeyTypes.contains(keyType)) {
                this.trackingDebug.put(keyType, obj);
            }
        }
    }
}
