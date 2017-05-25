package uy.com.fusion.library.rest.client;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uy.com.fusion.library.rest.client.metrics.MetricRegistry;
import uy.com.fusion.library.rest.client.metrics.MetricRegistryListener;
import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;

public abstract class InnerHttpClient {

    private List<MetricRegistryListener> metricRegistryListeners = Collections
        .synchronizedList(new LinkedList<MetricRegistryListener>());

    public void addListener(MetricRegistryListener listener) {
        this.metricRegistryListeners.add(listener);
    }

    public void dispatchMetric(MetricRegistry metric) {
        for (MetricRegistryListener listeners : this.metricRegistryListeners) {
            listeners.onMetric(metric);
        }
    }

    public abstract HttpResponseContext execute(HttpRequestContext request) throws IOException;

    public abstract void shutdown();

}
