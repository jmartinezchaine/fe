package uy.com.fusion.library.rest.client.apache;

import uy.com.fusion.library.rest.client.InnerHttpClient;
import uy.com.fusion.library.rest.client.InnerHttpClientBuilder;
import uy.com.fusion.library.rest.config.RestConnectorConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public class InnerApacheHttpClientBuilder
        implements InnerHttpClientBuilder {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    protected RestConnectorConfig config;
    protected SSLContext sslContext;

    @Override
    public void setRestConnectorConfig(RestConnectorConfig config) {
        this.config = config;
    }

    @Override
    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public InnerHttpClient build() {
        RequestConfig requestConfig = this.buildStandardRequestConfig(this.config).build();
        return new InnerApacheHttpClient(this.buildHttpClient(requestConfig).build(), requestConfig);
    }


    // //////////////////////////////////////////////
    // BUILDERS
    // //////////////////////////////////////////////

    protected RequestConfig.Builder buildStandardRequestConfig(RestConnectorConfig config) {
        return RequestConfig.copy(RequestConfig.DEFAULT).setMaxRedirects(3).setConnectionRequestTimeout(-1)
                .setConnectTimeout((int) config.getConnectionTimeout()).setSocketTimeout((int) config.getReadTimeout());
    }

    protected HttpClientBuilder buildHttpClient(RequestConfig requestConfig) {
        return HttpClients.custom().setConnectionManager(this.buildConnectionManager(this.config))
                .setRequestExecutor(new HttpRequestExecutorWithMetrics())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(this.config.getRequestMaxRetries(), false))
                .setDefaultRequestConfig(requestConfig);
    }

    protected PoolingHttpClientConnectionManager buildConnectionManager(RestConnectorConfig config) {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(this
                .buildConnectionSocketFactoryRegistry().build());
        connManager.setDefaultSocketConfig(this.buildSocketConfig().build());
        connManager.setValidateAfterInactivity((int) config.getValidateAfterInactivity());
        connManager.setMaxTotal(config.getMaxConnections());
        connManager.setDefaultMaxPerRoute(config.getMaxConnections());
        return connManager;
    }

    protected RegistryBuilder<ConnectionSocketFactory> buildConnectionSocketFactoryRegistry() {
        RegistryBuilder<ConnectionSocketFactory> connSocketFactoryRegistryBuilder = RegistryBuilder.create();
        if (this.sslContext == null) {
            this.sslContext = SSLContexts.createDefault();
        }
        HostnameVerifier hostnameVerifier = getHostnameVerifier();
        connSocketFactoryRegistryBuilder.register(HTTPS, new SSLConnectionSocketFactory(this.sslContext, hostnameVerifier));
        connSocketFactoryRegistryBuilder.register(HTTP, PlainConnectionSocketFactory.INSTANCE);

        return connSocketFactoryRegistryBuilder;
    }

    protected HostnameVerifier getHostnameVerifier() {
        return SSLConnectionSocketFactory.getDefaultHostnameVerifier();
    }

    protected SocketConfig.Builder buildSocketConfig() {
        return SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(true)
                .setSoTimeout((int) this.config.getReadTimeout());
    }

}
