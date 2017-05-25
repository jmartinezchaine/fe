package uy.com.fusion.library.rest.interceptors.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.interceptors.HttpRequestContext;
import uy.com.fusion.library.rest.interceptors.HttpResponseContext;
import uy.com.fusion.library.rest.interceptors.Interceptor;
import uy.com.fusion.library.rest.utils.DependencyUtils;

public class AcceptHessianInterceptor
    extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptHessianInterceptor.class);
    private static final String APPLICATION_HESSIAN = "application/xhessian";
    private static final boolean HESSIAN_AVAILABLE = DependencyUtils.isClassPresent("com.caucho.hessian.io.Hessian2Input");

    @Override
    protected HttpResponseContext preHandle(HttpRequestContext request) throws IOException {

        if (HESSIAN_AVAILABLE) {
            String acceptHeader = request.getHeaders().getAccept();
            if (acceptHeader == null) {
                LOGGER.debug("Setting header 'Accept: application/x-hessian'.");
                request.getHeaders().setAccept(APPLICATION_HESSIAN);
            } else if (!acceptHeader.contains(APPLICATION_HESSIAN)) {
                LOGGER.debug("Adding 'application/x-hessian' to 'Accept' header.");
                request.getHeaders().setAccept(acceptHeader + ", " + APPLICATION_HESSIAN);
            }
        }
        return this.next(request);
    }

}
