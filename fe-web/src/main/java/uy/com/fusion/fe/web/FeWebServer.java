package uy.com.fusion.fe.web;

import java.util.EnumSet;
import java.util.TimeZone;

import javax.servlet.DispatcherType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.joda.time.DateTimeZone;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import uy.com.fusion.fe.web.util.springfox.SpringFoxUrlFilter;
import uy.com.fusion.fe.web.context.WebContext;
import uy.com.fusion.fe.web.util.springfox.jetty.JettyServerFactory;

public class FeWebServer {
    private static final String GMT = "GMT";
    private static final String CONTEXT_NAME = "/";
    private static final int DEFAULT_PORT = 9290;

    private final Server server;

    public FeWebServer(String[] args) {
        this.server = this.createNewServer(args);
    }

    public static void main(String[] args) throws Exception {
        FeWebServer server = new FeWebServer(args);
        server.run();
    }

    private Server createNewServer(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        String contextPath = args.length > 1 ? args[1] : CONTEXT_NAME;

        TimeZone.setDefault(TimeZone.getTimeZone(GMT));
        DateTimeZone.setDefault(DateTimeZone.UTC);

        GzipHandler handler = this.buildWebAppContext(args, contextPath);

        return JettyServerFactory.buildServer(handler, port);
    }

    private void run() throws Exception {
        this.server.start();
        this.server.join();
    }

    private GzipHandler buildWebAppContext(String[] args, String contextPath) {

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(WebContext.class);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.setContextPath(contextPath);

        this.appendSpringDispatcherServlet(applicationContext, handler);
        this.appendListeners(applicationContext, handler);
        this.appendFilters(handler);

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(handler);

        applicationContext.close();
        return gzipHandler;
    }

    private void appendListeners(AnnotationConfigWebApplicationContext applicationContext, ServletContextHandler handler) {
        // Para que funcione Spring con su contexto
        handler.addEventListener(new ContextLoaderListener(applicationContext) {
            @Override
            public void contextInitialized(javax.servlet.ServletContextEvent event) {
                super.contextInitialized(event);
            }
        });
    }

    private void appendSpringDispatcherServlet(AnnotationConfigWebApplicationContext applicationContext, ServletContextHandler handler) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext) {
            private static final long serialVersionUID = 1L;

            @Override
            public void init(ServletConfig config) throws ServletException {
                super.init(config);
            }
        };
        dispatcherServlet.setDispatchOptionsRequest(true);
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        servletHolder.setName("spring");
        servletHolder.setInitOrder(1);
        handler.addServlet(servletHolder, "/*");
    }

    private void appendFilters(ServletContextHandler handler) {

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        FilterHolder characterEncodingFilterHolder = new FilterHolder(characterEncodingFilter);
        handler.addFilter(characterEncodingFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));

        FilterHolder filterHolder = new FilterHolder(new SpringFoxUrlFilter());
        handler.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
    }
}
