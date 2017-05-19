package uy.com.fusion.fe.web.context.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class WebPropertiesConfig {

    @Bean(name = "web.properties")
    public PropertyConfigurerDescriptor getProperties() {
        PropertyConfigurerDescriptor properties = new PropertyConfigurerDescriptor();
        properties.setApplicationResource(new ClassPathResource("conf/desa.properties"));
        //properties.setApplicationResource(new ClassPathResource("conf/prod.properties"));
        return properties;
    }
}
