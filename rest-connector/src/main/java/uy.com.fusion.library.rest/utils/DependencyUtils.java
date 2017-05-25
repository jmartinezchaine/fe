package uy.com.fusion.library.rest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyUtils.class);

    public static boolean isClassPresent(String classname) {
        try {
            Class.forName(classname);
            return true;
        } catch (Exception e) {
            LOGGER.debug(String.format("Class %s not available", classname));
            return false;
        }
    }
}
