package uy.com.fusion.tech.rest.connector.client.interceptor;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;

public class CustomHeadersInterceptor
    extends Interceptor {

    private Map<String, String> headers;

    public CustomHeadersInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    public CustomHeadersInterceptor() {
        this.headers = null;
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        if (MapUtils.isNotEmpty(this.headers)) {
            this.headers.entrySet()
                .forEach(entry ->
                    {
                        String headerName = entry.getKey();
                        String value = entry.getValue();

                        request.getHeaders()
                            .set(headerName, value);
                    });
        }

        return this.next(request);
    }
}
