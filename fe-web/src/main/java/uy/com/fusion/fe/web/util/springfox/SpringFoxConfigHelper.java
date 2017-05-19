package uy.com.fusion.fe.web.util.springfox;

import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import springfox.documentation.swagger2.web.Swagger2Controller;
import uy.com.fusion.fe.web.util.springfox.annotations.EnableSpringFox;

/**
 * Created by juanmartinez on 18/5/17.
 */
public abstract class SpringFoxConfigHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringFoxConfigHelper.class);

    private static String path = null;

    static {
        Reflections reflections = new Reflections("uy.com.fusion");
        Set<Class<?>> clazzes = reflections.getTypesAnnotatedWith(EnableSpringFox.class);
        clazzes.stream().map(c -> c.getAnnotation(EnableSpringFox.class)).filter(Objects::nonNull).map(EnableSpringFox::value).findFirst().ifPresent(path -> {
            LOGGER.info("Springfox Enable!");
            SpringFoxConfigHelper.path = path;
        });
    }

    public static String path() {
        return path;
    }

    public static String apiPath() {
        return Swagger2Controller.DEFAULT_URL;
    }

    public static boolean isEnable() {
        return !StringUtils.isEmpty(path);
    }

}
