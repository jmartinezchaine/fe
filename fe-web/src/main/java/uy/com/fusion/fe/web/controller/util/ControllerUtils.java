package uy.com.fusion.fe.web.controller.util;

import java.text.MessageFormat;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtils.class);

    private static final String MESSAGE_NOT_FOUND = "{0} with id={1} not found.";

    public static String buildNotFoundMesage(String entity, Object id) {
        return MessageFormat.format(MESSAGE_NOT_FOUND, entity, id);
    }

    public static Integer configurePaging(Integer value) {
        if (value != null) {
            return NumberUtils.max(value, NumberUtils.INTEGER_ZERO);
        }
        return NumberUtils.INTEGER_ZERO;
    }

    public static Integer configureLimit(Integer value) {
        if (value != null) {
            return NumberUtils.max(value, 10);
        }
        return 10;
    }
}
