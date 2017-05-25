package uy.com.fusion.library.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;



public class HttpStatus {

    // --- 1xx Informational ---
    public static final HttpStatus CONTINUE = new HttpStatus(100, "Continue");

    // --- 2xx Success ---
    public static final HttpStatus OK = new HttpStatus(200, "Ok");
    public static final HttpStatus CREATED = new HttpStatus(201, "Created");
    public static final HttpStatus ACCEPTED = new HttpStatus(202, "Accepted");
    public static final HttpStatus NON_AUTHORITATIVE_INFORMATION = new HttpStatus(203, "Non Authoritative Information");
    public static final HttpStatus NO_CONTENT = new HttpStatus(204, "No Content");
    public static final HttpStatus RESET_CONTENT = new HttpStatus(205, "Reset Content");
    public static final HttpStatus PARTIAL_CONTENT = new HttpStatus(206, "Partial Content");
    public static final HttpStatus MULTI_STATUS = new HttpStatus(207, "Multi Status");
    public static final HttpStatus ALREADY_REPORTED = new HttpStatus(208, "Already Reported");

    // --- 3xx Redirection ---
    public static final HttpStatus MULTIPLE_CHOICES = new HttpStatus(300, "Multiple Choices");
    public static final HttpStatus MOVED_PERMANENTLY = new HttpStatus(301, "Moved Permanently");
    public static final HttpStatus MOVED_TEMPORARILY = new HttpStatus(302, "Moved Temporarily");
    public static final HttpStatus SEE_OTHER = new HttpStatus(303, "See Other");
    public static final HttpStatus NOT_MODIFIED = new HttpStatus(304, "Not Modified");
    public static final HttpStatus USE_PROXY = new HttpStatus(305, "Use Proxy");
    public static final HttpStatus TEMPORARY_REDIRECT = new HttpStatus(307, "Temporary Redirect");

    // --- 4xx Client Error ---
    public static final HttpStatus BAD_REQUEST = new HttpStatus(400, "Bad Request");
    public static final HttpStatus UNAUTHORIZED = new HttpStatus(401, "Unauthorized");
    public static final HttpStatus PAYMENT_REQUIRED = new HttpStatus(402, "Payment Required");
    public static final HttpStatus FORBIDDEN = new HttpStatus(403, "Forbidden");
    public static final HttpStatus NOT_FOUND = new HttpStatus(404, "Not Found");
    public static final HttpStatus METHOD_NOT_ALLOWED = new HttpStatus(405, "Method Not Allowed");
    public static final HttpStatus NOT_ACCEPTABLE = new HttpStatus(406, "Not Acceptable");
    public static final HttpStatus PROXY_AUTHENTICATION_REQUIRED = new HttpStatus(407, "Proxy Authentication Required");
    public static final HttpStatus REQUEST_TIMEOUT = new HttpStatus(408, "Request Timeout");
    public static final HttpStatus CONFLICT = new HttpStatus(409, "Conflict");
    public static final HttpStatus GONE = new HttpStatus(410, "Gone");
    public static final HttpStatus LENGTH_REQUIRED = new HttpStatus(411, "Length Required");
    public static final HttpStatus PRECONDITION_FAILED = new HttpStatus(412, "Precondition Failed");
    public static final HttpStatus REQUEST_TOO_LONG = new HttpStatus(413, "Request Too Long");
    public static final HttpStatus REQUEST_URI_TOO_LONG = new HttpStatus(414, "Request Uri Too Long");
    public static final HttpStatus UNSUPPORTED_MEDIA_TYPE = new HttpStatus(415, "Unsupported Media Type");
    public static final HttpStatus REQUESTED_RANGE_NOT_SATISFIABLE = new HttpStatus(416, "Requested Range Not Satisfiable");
    public static final HttpStatus EXPECTATION_FAILED = new HttpStatus(417, "Expectation Failed");
    public static final HttpStatus INSUFFICIENT_SPACE_ON_RESOURCE = new HttpStatus(419, "Insufficient Space On Resource");
    public static final HttpStatus METHOD_FAILURE = new HttpStatus(420, "Method Failure");
    public static final HttpStatus LOCKED = new HttpStatus(423, "Locked");
    public static final HttpStatus FAILED_DEPENDENCY = new HttpStatus(424, "Failed Dependency");

    // --- 5xx Server Error ---
    public static final HttpStatus INTERNAL_SERVER_ERROR = new HttpStatus(500, "Internal Server Error");
    public static final HttpStatus NOT_IMPLEMENTED = new HttpStatus(501, "Not Implemented");
    public static final HttpStatus BAD_GATEWAY = new HttpStatus(502, "Bad Gateway");
    public static final HttpStatus SERVICE_UNAVAILABLE = new HttpStatus(503, "Service Unavailable");
    public static final HttpStatus GATEWAY_TIMEOUT = new HttpStatus(504, "Gateway Timeout");
    public static final HttpStatus HTTP_VERSION_NOT_SUPPORTED = new HttpStatus(505, "Http Version Not Supported");
    public static final HttpStatus INSUFFICIENT_STORAGE = new HttpStatus(507, "Insufficient Storage");

    @SuppressWarnings("serial")
    private static final Map<Integer, HttpStatus> VALUES = new HashMap<Integer, HttpStatus>() {
        {
            this.put(CONTINUE.getCode(), CONTINUE);
            this.put(OK.getCode(), OK);
            this.put(CREATED.getCode(), CREATED);
            this.put(ACCEPTED.getCode(), ACCEPTED);
            this.put(NON_AUTHORITATIVE_INFORMATION.getCode(), NON_AUTHORITATIVE_INFORMATION);
            this.put(NO_CONTENT.getCode(), NO_CONTENT);
            this.put(RESET_CONTENT.getCode(), RESET_CONTENT);
            this.put(PARTIAL_CONTENT.getCode(), PARTIAL_CONTENT);
            this.put(MULTI_STATUS.getCode(), MULTI_STATUS);
            this.put(MULTIPLE_CHOICES.getCode(), MULTIPLE_CHOICES);
            this.put(MOVED_PERMANENTLY.getCode(), MOVED_PERMANENTLY);
            this.put(MOVED_TEMPORARILY.getCode(), MOVED_TEMPORARILY);
            this.put(SEE_OTHER.getCode(), SEE_OTHER);
            this.put(NOT_MODIFIED.getCode(), NOT_MODIFIED);
            this.put(USE_PROXY.getCode(), USE_PROXY);
            this.put(TEMPORARY_REDIRECT.getCode(), TEMPORARY_REDIRECT);
            this.put(BAD_REQUEST.getCode(), BAD_REQUEST);
            this.put(UNAUTHORIZED.getCode(), UNAUTHORIZED);
            this.put(PAYMENT_REQUIRED.getCode(), PAYMENT_REQUIRED);
            this.put(FORBIDDEN.getCode(), FORBIDDEN);
            this.put(NOT_FOUND.getCode(), NOT_FOUND);
            this.put(METHOD_NOT_ALLOWED.getCode(), METHOD_NOT_ALLOWED);
            this.put(NOT_ACCEPTABLE.getCode(), NOT_ACCEPTABLE);
            this.put(PROXY_AUTHENTICATION_REQUIRED.getCode(), PROXY_AUTHENTICATION_REQUIRED);
            this.put(REQUEST_TIMEOUT.getCode(), REQUEST_TIMEOUT);
            this.put(CONFLICT.getCode(), CONFLICT);
            this.put(GONE.getCode(), GONE);
            this.put(LENGTH_REQUIRED.getCode(), LENGTH_REQUIRED);
            this.put(PRECONDITION_FAILED.getCode(), PRECONDITION_FAILED);
            this.put(REQUEST_TOO_LONG.getCode(), REQUEST_TOO_LONG);
            this.put(REQUEST_URI_TOO_LONG.getCode(), REQUEST_URI_TOO_LONG);
            this.put(UNSUPPORTED_MEDIA_TYPE.getCode(), UNSUPPORTED_MEDIA_TYPE);
            this.put(REQUESTED_RANGE_NOT_SATISFIABLE.getCode(), REQUESTED_RANGE_NOT_SATISFIABLE);
            this.put(EXPECTATION_FAILED.getCode(), EXPECTATION_FAILED);
            this.put(INSUFFICIENT_SPACE_ON_RESOURCE.getCode(), INSUFFICIENT_SPACE_ON_RESOURCE);
            this.put(METHOD_FAILURE.getCode(), METHOD_FAILURE);
            this.put(LOCKED.getCode(), LOCKED);
            this.put(FAILED_DEPENDENCY.getCode(), FAILED_DEPENDENCY);
            this.put(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR);
            this.put(NOT_IMPLEMENTED.getCode(), NOT_IMPLEMENTED);
            this.put(BAD_GATEWAY.getCode(), BAD_GATEWAY);
            this.put(SERVICE_UNAVAILABLE.getCode(), SERVICE_UNAVAILABLE);
            this.put(GATEWAY_TIMEOUT.getCode(), GATEWAY_TIMEOUT);
            this.put(HTTP_VERSION_NOT_SUPPORTED.getCode(), HTTP_VERSION_NOT_SUPPORTED);
            this.put(INSUFFICIENT_STORAGE.getCode(), INSUFFICIENT_STORAGE);
        }
    };

    public static enum Series {

        INFORMATIONAL(1), SUCCESSFUL(2), REDIRECTION(3), CLIENT_ERROR(4), SERVER_ERROR(5);

        private final int value;

        private Series(int value) {
            this.value = value;
        }

        /**
         * Return the integer value of this status series. Ranges from 1 to 5.
         */
        public int value() {
            return this.value;
        }

        public static Series valueOf(int status) {
            int seriesCode = status / 100;
            for (Series series : values()) {
                if (series.value == seriesCode) {
                    return series;
                }
            }
            throw new IllegalArgumentException("No matching constant for [" + status + "]");
        }

        public static Series valueOf(HttpStatus status) {
            return valueOf(status.code);
        }
    }

    public static void register(HttpStatus newHttpStatus) {
        if (VALUES.containsKey(newHttpStatus.code)) {
            throw new RuntimeException("HTTP STATUS " + newHttpStatus.code + " already registered");
        }
        VALUES.put(newHttpStatus.code, newHttpStatus);
    }


    private Series serie;
    private int code;
    private String reasonPhrase;

    private HttpStatus(int status, String reasonPhrase) {
        this.code = status;
        this.reasonPhrase = reasonPhrase;
        this.serie = Series.valueOf(this);
    }

    public int getCode() {
        return this.code;
    }

    public Series getSerie() {
        return this.serie;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public String toString() {
        return this.code + " " + this.reasonPhrase;
    }

    @Override
    public int hashCode() {
        return this.code;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HttpStatus)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        HttpStatus other = (HttpStatus) obj;

        return this.code == other.code;
    }

    public static HttpStatus valueOf(int statusCode) {
        HttpStatus httpStatus = VALUES.get(statusCode);
        return httpStatus != null ? httpStatus : new HttpStatus(statusCode, StringUtils.EMPTY);
    }

    public boolean isError() {
        return 400 <= this.code && this.code <= 599;
    }
}
