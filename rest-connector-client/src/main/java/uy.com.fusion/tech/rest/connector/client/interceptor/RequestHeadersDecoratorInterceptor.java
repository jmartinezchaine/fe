package uy.com.fusion.tech.rest.connector.client.interceptor;

import java.io.IOException;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;
import uy.com.fusion.tech.rest.connector.client.decorator.RequestHeadersDecorator;

public class RequestHeadersDecoratorInterceptor
    extends Interceptor {

    private final RequestHeadersDecorator decorator;

    public RequestHeadersDecoratorInterceptor(RequestHeadersDecorator decorator) {
        this.decorator = decorator;
    }

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        if (this.decorator != null) {
            this.decorator.decorate(request.getHeaders());
        }
        return this.next(request);
    }
}
