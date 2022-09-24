package me.mrfunny.configapi.annotations;

import net.bytebuddy.pool.TypePool;

import java.lang.annotation.*;

/**
 * <p>
 * Used to add a path to a config variable
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Path {
    String value();
}
