package me.mrfunny.configapi;

import me.mrfunny.configapi.impl.InterfaceInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * The main API class that used to create Bukkit configs from classes
 * <br>
 * To use it, create an interface and pass it to one of the {@link ConfigAPI#createConfig(JavaPlugin, Class)} methods.
 * <br>
 * Create methods, they will represent config variable names, return types will represent storage type of this method
 * <br>
 * You can use {@link me.mrfunny.configapi.annotations.Name} annotation, to give a custom config variable name yet same method name
 * <br>
 * You can use {@link me.mrfunny.configapi.annotations.Path} annotation, to add path to variable name in config
 *
 */
public class ConfigAPI {

    /**
     * <p>
     * Creates a dynamic instance of {@code configClass}
     * which is wrapper for bukkit config file in {@link JavaPlugin#getDataFolder()}
     * </p>
     * <br>
     * The default way to create a config wrapper instance
     * @param configClass The class you need to make a Bukkit config wrapper
     * @param plugin Used to get data folder of your plugin
     * @param <T> Your config class
     * @return Config wrapper instance
     */
    @NotNull
    public static <T> T createConfig(JavaPlugin plugin, Class<T> configClass){
        return createConfig(plugin.getConfig(), configClass);
    }

    /**
     * <p>
     * Creates a dynamic instance of {@code configClass}
     * which is wrapper for bukkit config file in {@code path}
     * </p>
     * <br>
     * Before creating config wrapper, it creates {@link FileConfiguration} instance from {@code path}
     * <br>
     * and then loads it as wrapper
     * @param configClass The class you need to make a Bukkit config wrapper
     * @param path Path to create {@code FileConfiguration} from it
     * @param <T> Your config class
     * @return Config wrapper instance
     */
    @NotNull
    public static <T> T createConfig(File path, Class<T> configClass){
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(path);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return createConfig(configuration, configClass);
    }

    /**
     * Creates a dynamic instance of {@code configClass}
     * which is wrapper for bukkit config file in {@link FileConfiguration#getCurrentPath()}
     * @param configClass The class you need to make a Bukkit config wrapper
     * @param config Already created {@link FileConfiguration}
     * @param <T> Your config class
     * @return Config wrapper instance
     */
    @NotNull
    public static <T> T createConfig(FileConfiguration config, Class<T> configClass){
        if(!configClass.isInterface()) {
            throw new IllegalStateException("configClass cannot be class. It should be only an interface");
        }
        Class<? extends T> createdImplementation = new ByteBuddy()
                .subclass(configClass)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new InterfaceInterceptor(config)))
                .make()
                .load(configClass.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER_PERSISTENT)
                .getLoaded();
        try {
            return createdImplementation.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
