package uy.com.fusion.library.rest.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.com.fusion.library.rest.RestConnector;

public class ShutdownHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);
    private static final ShutdownHook instance = new ShutdownHook();

    private final List<WeakReference<RestConnector>> restConnectors =
            Collections.synchronizedList(new ArrayList<WeakReference<RestConnector>>());

    private ShutdownHook() {
        init();
    }

    public static void attachShutdownHookTo(RestConnector r) {
        instance.restConnectors.add(new WeakReference<RestConnector>(r));
    }

    private void init() {
        Thread hook = new Thread("RestConnectorShutdownHook") {
            @Override
            public void run() {
                for (WeakReference<RestConnector> w : ShutdownHook.instance.restConnectors) {
                    try {
                        RestConnector r = w.get();
                        if (r != null) {
                            r.shutdown();
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error while shutting down rest connector", e);
                    }

                }
            }
        };
        Runtime.getRuntime().addShutdownHook(hook);
    }

}
