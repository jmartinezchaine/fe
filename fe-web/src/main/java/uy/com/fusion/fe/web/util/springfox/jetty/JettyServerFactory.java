package uy.com.fusion.fe.web.util.springfox.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * Created by juanmartinez on 18/5/17.
 */
@Configurable
public class JettyServerFactory {

    private static final QueuedThreadPool JETTY_THREAD_POOL = new QueuedThreadPool();

    public static Server buildServer(Handler handler, int port) {
        return buildServer(JETTY_THREAD_POOL, handler, port);
    }

    public static Server buildServer(ThreadPool pool, Handler handler, int port) {
        Server server = new Server(pool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});

        server.setHandler(handler);
        server.setStopAtShutdown(true);
        return server;
    }

    @Bean(name = "jettyThreadPool")
    public QueuedThreadPool getJettyThreadPool() {
        return JETTY_THREAD_POOL;
    }
}

