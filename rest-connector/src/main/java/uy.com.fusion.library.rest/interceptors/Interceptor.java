package uy.com.fusion.library.rest.interceptors;

import java.io.IOException;
import java.util.Map;

import uy.com.fusion.library.rest.client.InnerHttpClient;

public abstract class Interceptor {

    private Interceptor next;
    private InnerHttpClient client;

    public HttpResponseContext intercept(HttpRequestContext request) throws IOException {
        HttpResponseContext response;
        try {
            response = this.preHandle(request);
        } catch (IOException e) {
            return this.handleException(request, e);
        }
        return this.postHandle(response);
    }

    protected HttpResponseContext next(HttpRequestContext request) throws IOException {
        if (this.next != null) {
            return this.next.intercept(request);
        } else {
            HttpResponseContext response = this.client.execute(request);
            Map<String, Object> context = request.getAllContext();
            response.setAllContext(context);
            response.setContext("request", request);
            return response;
        }
    }

    /**
     * When overriding preHandle, this.next should always be called unless there is a specific need for breaking the interceptor chain. 
     * 
     * @param request
     * @return
     * @throws IOException
     */
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {
        return this.next(request);
    }

    protected HttpResponseContext postHandle(HttpResponseContext response) throws IOException {
        return response;
    }

    protected HttpResponseContext handleException(HttpRequestContext request, IOException e) throws IOException {
        throw e;
    }

    final void setNext(Interceptor next) {
        this.next = next;
    }

    final Interceptor getNext() {
        return this.next;
    }

    final void setClient(InnerHttpClient innerClient) {
        this.client = innerClient;
    }
}
