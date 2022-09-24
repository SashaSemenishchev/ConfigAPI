package me.mrfunny.configapi.impl;

import me.mrfunny.configapi.annotations.Name;
import me.mrfunny.configapi.annotations.Path;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;

public class InterfaceInterceptor {
    private final FileConfiguration config;

    public InterfaceInterceptor(FileConfiguration config) {
        this.config = config;
    }

    @RuntimeType
    public Object intercept(@Origin Method method,
                            @AllArguments Object[] args) throws Throwable {
        String namespace = null;
        if(method.isAnnotationPresent(Path.class)) {
            namespace = method.getAnnotation(Path.class).value();
        }
        String variableName = method.getName();
        if(method.isAnnotationPresent(Name.class)) {
            variableName = method.getAnnotation(Name.class).value();
        }
        String setter;
        if(namespace == null) {
            setter = variableName;
        } else {
            setter = namespace + "." + variableName;
        }
        if(args.length != 0) {
            config.set(setter, args[0]);
            return Void.TYPE;
        }
        return config.get(setter);
    }
}
