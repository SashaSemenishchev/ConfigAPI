package me.mrfunny.configapi.annotations;

import java.lang.annotation.*;

/**
 * <p>
 * Used to explicitly set a name of a config variable
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Name {
    String value();
}
