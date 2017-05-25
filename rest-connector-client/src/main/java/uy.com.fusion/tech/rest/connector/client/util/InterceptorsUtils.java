package uy.com.fusion.tech.rest.connector.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uy.com.fusion.library.rest.interceptors.Interceptor;
import uy.com.fusion.library.rest.interceptors.Interceptors;

public abstract class InterceptorsUtils {
    private static Field FIRST_INTERCEPTOR = null;
    private static Method GET_NEXT = null;
    private static Method SET_NEXT = null;

    static {
        try {
            FIRST_INTERCEPTOR = Interceptors.class.getDeclaredField("firstInterceptor");
            FIRST_INTERCEPTOR.setAccessible(true);

            GET_NEXT = Interceptor.class.getDeclaredMethod("getNext");
            GET_NEXT.setAccessible(true);

            SET_NEXT = Interceptor.class.getDeclaredMethod("setNext", Interceptor.class);
            SET_NEXT.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setAfterRoutingInterceptor(Interceptors interceptors, Interceptor newInterceptor) {
        try {
            Interceptor first = Interceptor.class.cast(FIRST_INTERCEPTOR.get(interceptors));
            Interceptor routingInterceptor = findRoutingInterceptor(first);

            if (routingInterceptor == null) {
                interceptors.addFirst(newInterceptor);
            } else {
                Interceptor nextInterceptor = Interceptor.class.cast(GET_NEXT.invoke(routingInterceptor));
                if (nextInterceptor == null) {
                    interceptors.add(newInterceptor);
                } else {
                    SET_NEXT.invoke(newInterceptor, nextInterceptor);
                    SET_NEXT.invoke(routingInterceptor, newInterceptor);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addFirst(Interceptors interceptors, Interceptor newInterceptor) {
        try {
            interceptors.addFirst(newInterceptor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Interceptor findRoutingInterceptor(Interceptor current) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (current == null) {
            return null;
        }
        return findRoutingInterceptor(Interceptor.class.cast(GET_NEXT.invoke(current)));
    }
}
