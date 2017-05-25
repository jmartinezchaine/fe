package uy.com.fusion.library.rest.builder;

public class PerRequestConfig {

    private Long timeoutInMillis;

    public PerRequestConfig() {

    }

    public PerRequestConfig(PerRequestConfig that) {
        this.timeoutInMillis = that.timeoutInMillis;
    }

    public Long getTimeoutInMillis() {
        return this.timeoutInMillis;
    }

    public void setTimeoutInMillis(Long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

}
