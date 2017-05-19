package uy.com.fusion.fe.web.util.springfox;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;

/**
 * Created by juanmartinez on 18/5/17.
 */
public class SpringFoxUrlFilter
                implements Filter {

    private static final String SPRINGFOX_UI_SEARCH_TOKEN = "swagger-ui";
    private static final String SPRINGFOX_RESOURCES_SEARCH_TOKEN = "swagger-resources";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SpringFoxConfigHelper.isEnable()) {
            Request jettyRequest = (Request) request;
            HttpURI httpURI = jettyRequest.getHttpURI();
            String path = httpURI.getPath();
            if (path.contains(SPRINGFOX_UI_SEARCH_TOKEN) || path.contains(SPRINGFOX_RESOURCES_SEARCH_TOKEN) || path.equals(
                            SpringFoxConfigHelper.path() + SpringFoxConfigHelper.apiPath())) {
                httpURI.setPath(path.replaceFirst(SpringFoxConfigHelper.path(), ""));
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
