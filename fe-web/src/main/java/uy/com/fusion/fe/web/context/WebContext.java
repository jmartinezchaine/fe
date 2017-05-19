package uy.com.fusion.fe.web.context;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import uy.com.fusion.fe.web.util.springfox.SpringFoxConfigHelper;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan({"uy.com.fusion.fe"})
public class WebContext
                extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (SpringFoxConfigHelper.isEnable()) {
            registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }
}
