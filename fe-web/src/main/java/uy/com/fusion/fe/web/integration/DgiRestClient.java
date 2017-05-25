package uy.com.fusion.fe.web.integration;

import java.util.Map;

import javax.net.ssl.SSLContext;

import uy.com.fusion.library.rest.serializers.json.JsonConfig;
import uy.com.fusion.library.rest.utils.TypeReference;
import uy.com.fusion.tech.rest.connector.client.RestConnectorClient;


/**
 * Created by juanmartinez on 31/5/16.
 */
public class DgiRestClient
                extends RestConnectorClient {
    private static final String SERVICE_TYPE = "DGI";

    private static final String PATH_TEST = "health-check";
    private static final TypeReference<Map<String, Object>> RESPONSE_TYPE = new TypeReference<Map<String, Object>>() {
    };

    public DgiRestClient(String protocol, String host, String context, JsonConfig.JsonFormat jsonFormat, Long connectionTimeout, Long readTimeout,
                    Long idleConnectionTimeout, Integer maxConnections, String clientId, SSLContext sslContext) {
        super(protocol, host, context, jsonFormat, connectionTimeout, readTimeout, idleConnectionTimeout, maxConnections, clientId, sslContext);
    }

    public Map<String, Object> test(String code) {
        return this.doGet(PATH_TEST, RESPONSE_TYPE, code);
    }

    @Override
    protected String getServiceType() {
        return SERVICE_TYPE;
    }
}
