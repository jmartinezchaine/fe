package uy.com.fusion.library.rest.client;

import javax.net.ssl.SSLContext;

import uy.com.fusion.library.rest.config.RestConnectorConfig;

public interface InnerHttpClientBuilder {

    void setRestConnectorConfig(RestConnectorConfig config);
    
    void setSslContext(SSLContext sslContext);

    InnerHttpClient build();

}
