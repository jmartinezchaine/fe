package uy.com.fusion.tech.rest.connector.client.newrelic;

public class NewRelicDataHolder {
    private String serviceUrl;
    private String serviceType;
    private Long serviceTime;
    private String httpVerb;
    private String trace;
    private String body;
    private boolean microbalanced;
    private boolean lookoutTraced;
    private String headers;
    private String uriParams;

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public NewRelicDataHolder serviceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        return this;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public NewRelicDataHolder serviceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public String getTrace() {
        return this.trace;
    }

    public NewRelicDataHolder trace(String trace) {
        this.trace = trace;
        return this;
    }

    public Long getServiceTime() {
        return this.serviceTime;
    }

    public NewRelicDataHolder serviceTime(Long serviceTime) {
        this.serviceTime = serviceTime;
        return this;
    }

    public String getBody() {
        return this.body;
    }

    public NewRelicDataHolder body(String body) {
        this.body = body;
        return this;
    }

    public boolean isMicrobalanced() {
        return this.microbalanced;
    }

    public NewRelicDataHolder microbalanced(boolean microbalanced) {
        this.microbalanced = microbalanced;
        return this;
    }

    public boolean isLookoutTraced() {
        return this.lookoutTraced;
    }

    public NewRelicDataHolder lookoutTraced(boolean lookoutTraced) {
        this.lookoutTraced = lookoutTraced;
        return this;
    }

    public String getHttpVerb() {
        return this.httpVerb;
    }

    public NewRelicDataHolder httpVerb(String httpVerb) {
        this.httpVerb = httpVerb;
        return this;
    }

    public String getHeaders() {
        return this.headers;
    }

    public NewRelicDataHolder headers(String headers) {
        this.headers = headers;
        return this;
    }

    public String getUriParams() {
        return this.uriParams;
    }

    public NewRelicDataHolder uriParams(String uriParams) {
        this.uriParams = uriParams;
        return this;
    }
}
