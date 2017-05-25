package uy.com.fusion.library.rest.client.metrics;

import java.util.HashMap;
import java.util.Map;

import uy.com.fusion.library.rest.client.InnerHttpClient;

public class MetricRegistry {


    public static MetricRegistryBuilder createBuilder(InnerHttpClient innerHttpClient) {
        return new MetricRegistryBuilder(innerHttpClient);
    }

    private final long timestamp;
    private final String requestMethod;
    private final String requestHost;
    private final String requestPath;
    private final Map<String, String> requestHeadersLowerCase;
    private final Long requestStart;
    private final int requestSize;
    private final Long responseStart;
    private final Integer responseStatusCode;
    private final Map<String, String> responseHeadersLowerCase;
    private final Exception responseException;
    private final Long responseEnd;
    private final int responseSize;
    private final Map<String, Object> customMetrics;

    private MetricRegistry(long timestamp, String requestMethod, String requestHost, String requestPath,
                           Map<String, String> requestHeadersLowerCase, Long requestStart, int requestSize,
                           Long responseStart, Integer responseStatusCode, Map<String, String> responseHeadersLowerCase,
                           Exception responseException, Long responseEnd, int responseSize,
                           Map<String, Object> customMetrics) {
        this.timestamp = timestamp;
        this.requestMethod = requestMethod;
        this.requestHost = requestHost;
        this.requestPath = requestPath;
        this.requestHeadersLowerCase = new HashMap<>(requestHeadersLowerCase);
        this.requestStart = requestStart;
        this.requestSize = requestSize;
        this.responseStart = responseStart;
        this.responseStatusCode = responseStatusCode;
        this.responseHeadersLowerCase = responseHeadersLowerCase;
        this.responseException = responseException;
        this.responseEnd = responseEnd;
        this.responseSize = responseSize;
        this.customMetrics = new HashMap<>(customMetrics);
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public String getRequestHost() {
        return this.requestHost;
    }

    public String getRequestPath() {
        return this.requestPath;
    }

    public Map<String, String> getRequestHeadersLowerCase() {
        return this.requestHeadersLowerCase;
    }

    public Long getRequestStart() {
        return this.requestStart;
    }

    public int getRequestSize() {
        return this.requestSize;
    }

    public Long getResponseStart() {
        return this.responseStart;
    }

    public Integer getResponseStatusCode() {
        return this.responseStatusCode;
    }

    public Exception getResponseException() {
        return this.responseException;
    }

    public Map<String, String> getResponseHeadersLowerCase() {
        return this.responseHeadersLowerCase;
    }

    public Long getResponseEnd() {
        return this.responseEnd;
    }

    public Long getRoundtripTime() {
        return this.responseStart - this.requestStart;
    }

    public Long getFullRoundtripTime() {
        return this.responseEnd - this.requestStart;
    }

    public int getResponseSize() {
        return this.responseSize;
    }

    public <T> T getCustomMetric(String key, Class<T> returnType) {
        if (customMetrics.containsKey(key)) {
            return returnType.cast(customMetrics.get(key));
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "MetricRegistry [requestStart=" + this.requestStart + ", responseStart=" + this.responseStart
                + ", responseEnd=" + this.responseEnd + "]";
    }

    public static class MetricRegistryBuilder {
        private final InnerHttpClient innerHttpClient;
        private long timestamp;
        private String requestMethod;
        private String requestHost;
        private String requestPath;
        private Map<String, String> requestHeadersLowerCase;
        private Long requestStart;
        private int requestSize;
        private Long responseStart;
        private Integer responseStatusCode;
        private Map<String, String> responseHeadersLowerCase;
        private Exception responseException;
        private Map<String, Object> customMetrics = new HashMap<>();

        private MetricRegistryBuilder(InnerHttpClient innerHttpClient) {
            this.innerHttpClient = innerHttpClient;
        }

        public MetricRegistryBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MetricRegistryBuilder withRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public MetricRegistryBuilder withRequestHost(String requestHost) {
            this.requestHost = requestHost;
            return this;
        }

        public MetricRegistryBuilder withRequestPath(String requestPath) {
            this.requestPath = requestPath;
            return this;
        }

        public MetricRegistryBuilder withRequestHeadersLowerCase(Map<String, String> requestHeaders) {
            this.requestHeadersLowerCase = new HashMap<>(requestHeaders);
            return this;
        }

        public MetricRegistryBuilder withRequestStartTimestamp(Long requestStart) {
            this.requestStart = requestStart;
            return this;
        }

        public MetricRegistryBuilder withRequestSize(int requestSize) {
            this.requestSize = requestSize;
            return this;
        }

        public MetricRegistryBuilder withResponseStartTimestamp(Long responseStart) {
            this.responseStart = responseStart;
            return this;
        }

        public MetricRegistryBuilder withResponseStatusCode(int code) {
            this.responseStatusCode = code;
            return this;
        }

        public MetricRegistryBuilder withResponseException(Exception e) {
            this.responseException = e;
            return this;
        }

        public MetricRegistryBuilder withResponseHeadersLowerCase(Map<String, String> responseHeadersLowerCase) {
            this.responseHeadersLowerCase = responseHeadersLowerCase;
            return this;
        }

        public MetricRegistryBuilder withCustomMetric(String key, Object value) {
            this.customMetrics.put(key, value);
            return this;
        }

        public void buildAndDispatch(int responseSize, Long responseEnd) {
            innerHttpClient.dispatchMetric(
                    new MetricRegistry(this.timestamp, this.requestMethod, this.requestHost, this.requestPath,
                            this.requestHeadersLowerCase, this.requestStart, this.requestSize, this.responseStart,
                            this.responseStatusCode, this.responseHeadersLowerCase, this.responseException, responseEnd,
                            responseSize, this.customMetrics));
        }
    }

}
