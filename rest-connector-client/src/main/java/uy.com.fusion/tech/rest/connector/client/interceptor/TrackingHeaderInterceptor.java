package uy.com.fusion.tech.rest.connector.client.interceptor;

import java.io.IOException;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;

public class TrackingHeaderInterceptor
                extends Interceptor {

    public TrackingHeaderInterceptor() {
        super();
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        /*TrackingDataBuilder builder = TrackingDataBuilderHelper.get();
        if (builder != null) {
            HttpHeaders headers = request.getHeaders();
            builder.headers(headers.toMap());
        }*/

        return super.preHandle(request);
    }
}
