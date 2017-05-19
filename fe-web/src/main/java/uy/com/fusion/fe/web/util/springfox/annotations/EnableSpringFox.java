package uy.com.fusion.fe.web.util.springfox.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by juanmartinez on 18/5/17.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableSwagger2
public @interface EnableSpringFox {

    String value() default "";

}
