package uy.com.fusion.tech.rest.connector.client.newrelic;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.MDC;

import uy.com.fusion.tech.rest.connector.client.RestConnectorClient.RequestType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public abstract class NewRelicReporter {

    // Se utiliza el mismo executor para todos los RestConnectors
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime()
        .availableProcessors() * 2);

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void appendNRCommonParams(NewRelicDataHolder newRelicDataHolder, String serviceUrl, String serviceType, boolean microbalanced,
        boolean lookoutTraced, RequestType type, Map<String, String> headers, String[] uriParams) {

        String httpVerb = type.name();
        String params = null;
        String headersMap = null;

        if (uriParams != null) {
            params = Arrays.toString(uriParams);
        }

        if (headers != null) {
            headersMap = headers.entrySet()
                .stream()
                .map(header -> header.getKey() + "=" + header.getValue())
                .collect(Collectors.joining(", "));
        }

        newRelicDataHolder.serviceUrl(serviceUrl)
            .serviceType(serviceType)
            .microbalanced(microbalanced)
            .lookoutTraced(lookoutTraced)
            .httpVerb(httpVerb)
            .headers(headersMap)
            .uriParams(params);

        appendNRCommonParams(serviceUrl, serviceType, microbalanced, lookoutTraced, httpVerb, headersMap, params);
    }

    public static void appendNRTrace(NewRelicDataHolder newRelicDataHolder, Exception ex) {
        String trace = ExceptionUtils.getFullStackTrace(ex);
        newRelicDataHolder.trace(trace);
        appendNRTrace(trace);
    }

    public static void appendNRserviceTime(NewRelicDataHolder newRelicDataHolder, long serviceTime) {
        newRelicDataHolder.serviceTime(serviceTime);
        appendNRserviceTime(serviceTime);
    }

    public static void notify(NewRelicDataHolder newRelicDataHolder) {
        String uow = MDC.get("uow");
        executor.execute(new Runnable() {
            @Override
            @Trace(metricName = "notify-new-relic-data", dispatcher = true)
            public void run() {
                appendNRReporterParams(uow);
                appendNRCommonParams(newRelicDataHolder.getServiceUrl(), newRelicDataHolder.getServiceType(), newRelicDataHolder.isMicrobalanced(),
                    newRelicDataHolder.isLookoutTraced(), newRelicDataHolder.getHttpVerb(), newRelicDataHolder.getHeaders(), newRelicDataHolder.getUriParams());
                appendNRTrace(newRelicDataHolder.getTrace());
                appendNRBody(newRelicDataHolder.getBody());
                appendNRserviceTime(newRelicDataHolder.getServiceTime());
            }
        });
    }

    public static synchronized void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private static void appendNRReporterParams(String uow) {
        NewRelic.addCustomParameter("byRestConnectorClient", 1);
        NewRelic.addCustomParameter("uow", uow);
    }

    protected static void appendNRTrace(String trace) {
        NewRelic.addCustomParameter("trace", trace);
    }

    protected static void appendNRserviceTime(long serviceTime) {
        NewRelic.addCustomParameter("serviceTime", serviceTime);
    }

    public static void appendNRBody(String responseBodyAsString) {
        NewRelic.addCustomParameter("body", responseBodyAsString);
    }

    public static void appendNRHeadersMapDecorated(String headersMapDecorated) {
        NewRelic.addCustomParameter("headersMapDecorated", headersMapDecorated);
    }

    protected static void appendNRCommonParams(String serviceUrl, String serviceType, boolean microbalanced, boolean lookoutTraced, String httpVerb,
        String headersMap, String uriParams) {
        NewRelic.addCustomParameter("microbalanced", BooleanUtils.toInteger(microbalanced));
        NewRelic.addCustomParameter("lookoutTraced", BooleanUtils.toInteger(lookoutTraced));
        NewRelic.addCustomParameter("serviceUrl", serviceUrl);
        NewRelic.addCustomParameter("httpVerb", httpVerb);
        NewRelic.addCustomParameter("serviceType", serviceType);
        NewRelic.addCustomParameter("uriParamsList", uriParams);
        NewRelic.addCustomParameter("headersMap", headersMap);
    }
}
