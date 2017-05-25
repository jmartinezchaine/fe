package uy.com.fusion.library.rest.interceptors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uy.com.fusion.library.rest.client.InnerHttpClient;
import uy.com.fusion.library.rest.config.RestConnectorConfig;
import uy.com.fusion.library.rest.interceptors.impl.AcceptJsonInterceptor;
import uy.com.fusion.library.rest.utils.Assert;

public class Interceptors {

    private Interceptor firstInterceptor;
    private Interceptor lastInterceptor;
    private int size = 1;

    private Interceptors() {
        DummyInterceptor di = new DummyInterceptor();
        this.firstInterceptor = di;
        this.lastInterceptor = di;
    }

    public Interceptors addFirst(Interceptor firstInterceptor) {
        Assert.notNull(firstInterceptor, "'firstInterceptor' is null");
        this.size++;
        firstInterceptor.setNext(this.firstInterceptor);
        this.firstInterceptor = firstInterceptor;
        return this;
    }

    public Interceptors add(Interceptor nextInterceptor) {
        Assert.notNull(nextInterceptor, "'nextInterceptor' is null");
        this.size++;
        this.lastInterceptor.setNext(nextInterceptor);
        this.lastInterceptor = nextInterceptor;

        return this;
    }

    public Interceptors add(Interceptor interceptor, int index) {
        Assert.notNull(interceptor, "'interceptor' is null");
        Assert.isTrue(0 <= index && index <= this.size, "'index' is out of bounds. 0<= index<= Interceptors.size())");

        if (index == 0) {
            this.addFirst(interceptor);
        } else if (index == this.size) {
            this.add(interceptor);
        } else {
            Interceptor it = this.firstInterceptor;
            int count = 1;

            while (count < index) {
                it = it.getNext();
                count++;
            }

            // count == index
            // add after it
            interceptor.setNext(it.getNext());
            it.setNext(interceptor);
            this.size++;
        }

        return this;
    }

    public int size() {
        return this.size - 1; // to hide DummyInterceptor
    }

    public HttpResponseContext interceptAndExecute(HttpRequestContext request, InnerHttpClient innerClient) throws IOException {
        this.lastInterceptor.setClient(innerClient);
        return this.firstInterceptor.intercept(request);
    }

    private static class DummyInterceptor
                    extends Interceptor {
    }


    // //////////////////////////////////////////////
    // STATIC FACTORY METHODS & BUILDER
    // //////////////////////////////////////////////

    public static Interceptors create(List<Interceptor> interceptors) {
        Interceptors ii = new Interceptors();
        for (Interceptor i : interceptors) {
            ii.add(i);
        }
        return ii;
    }

    public static Interceptors create(Interceptor... interceptors) {
        Interceptors ii = new Interceptors();
        for (Interceptor i : interceptors) {
            ii.add(i);
        }
        return ii;
    }

    public static Interceptors createJsonGzipfusion() {
        return Interceptors.Builder.create().addJsonGzip().build();
    }

    public static class Builder {

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(RestConnectorConfig config) {
            Builder builder = new Builder();
            builder.config = config;
            return builder;
        }

        private RestConnectorConfig config;
        private List<Interceptor> interceptors = new ArrayList<Interceptor>();

        public Builder addAll(List<Interceptor> interceptors) {
            this.interceptors.addAll(interceptors);
            return this;
        }

        public Builder add(Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        /**
         * Interceptors: [AcceptJson, GzipRequest, GzipResponse]
         */
        public Builder addJsonGzip() {
            Collections.addAll(interceptors, new AcceptJsonInterceptor());
            //new GzipRequestBodyInterceptor(),
            //new GzipResponseBodyInterceptor(true));

            return this;
        }

        public Interceptors build() {
            return Interceptors.create(interceptors);
        }

        private String configSafeGetString(String path) {
            if (config.getConfig().hasPath("rest-connector." + path)) {
                return config.getConfig().getString("rest-connector." + path);
            } else {
                return null;
            }
        }
    }
}
