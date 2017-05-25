package uy.com.fusion.library.rest.client.apache;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

import java.io.IOException;

import static uy.com.fusion.library.rest.client.apache.HttpContextMetricsHelper.getMetricsBuilder;

public class HttpRequestExecutorWithMetrics extends HttpRequestExecutor {
    @Override
    public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
        try {
            // we'll overwrite request-start-timestamp as this is actually a more accurate one
            getMetricsBuilder(context).withRequestStartTimestamp(System.nanoTime());
            return super.execute(request, conn, context);
        } finally {
            getMetricsBuilder(context).withResponseStartTimestamp(System.nanoTime());
        }
    }
}
