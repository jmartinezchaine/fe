package uy.com.fusion.fe.web.context.config.rest;

public abstract class DefaultRestConfig {

    public static final String PROTOCOL_SECURE = "https";
    protected final String PROTOCOL = "http";
    protected final String clientId = "doom";
    protected final Long defaultReadTimeout = 3000l;
    protected final Long defaultConnectionTimeout = 3000l;
    protected final Long defaultIdleConnectionTimeout = 10000l;
    protected final int defaultMaxConnections = 20;

}
