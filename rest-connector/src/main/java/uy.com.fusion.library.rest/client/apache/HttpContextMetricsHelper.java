package uy.com.fusion.library.rest.client.apache;

import uy.com.fusion.library.rest.client.metrics.MetricRegistry;
import org.apache.http.protocol.HttpContext;

public class HttpContextMetricsHelper {

    public static final String HTTP_METRICS = "http.metrics";

    public static HttpContext withMetricsBuilder(HttpContext context, MetricRegistry.MetricRegistryBuilder builder) {
        context.setAttribute(HTTP_METRICS, builder);
        return context;
    }

    public static MetricRegistry.MetricRegistryBuilder getMetricsBuilder(HttpContext context) {
        return MetricRegistry.MetricRegistryBuilder.class.cast(context.getAttribute(HTTP_METRICS));
    }

}
