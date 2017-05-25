package uy.com.fusion.fe.web.context.config.rest;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uy.com.fusion.fe.web.integration.DgiRestClient;
import uy.com.fusion.library.rest.serializers.json.JsonConfig;


@Configuration
public class DgiConfig
                extends DefaultRestConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DgiConfig.class);

    @Value("${dgi.rest.host}")
    private String host;

    @Value("${dgi.rest.context}")
    private String context;

    @Value("${apisec.token}")
    private String token;

    @Value("${apisec.client}")
    private String client;

    @Bean(name = "dgiRestClient",
                    destroyMethod = "shutdown")
    public DgiRestClient getDgiRestClient() throws NoSuchAlgorithmException {
        try {
            DgiRestClient client = new DgiRestClient(PROTOCOL_SECURE, this.host, this.context, JsonConfig.JsonFormat.SNAKE_CASE, this.defaultConnectionTimeout,
                            this.defaultReadTimeout, this.defaultIdleConnectionTimeout, this.defaultMaxConnections, this.client, SSLContext.getDefault());
            return client;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

}
