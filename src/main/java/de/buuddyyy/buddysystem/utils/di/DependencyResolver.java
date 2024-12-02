package de.buuddyyy.buddysystem.utils.di;

import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

public final class DependencyResolver {

    private static final Map<Class<?>, Supplier<?>> FACTORIES = Maps.newHashMap();
    private static final Map<Class<?>, Object> SINGLETONS = Maps.newHashMap();

    public static <T> void registerSingleton(Class<T> clazz, T obj) {
        FACTORIES.put(clazz, () -> obj);
    }

    public static <T> void registerLazySingleton(Class<T> clazz, Supplier<T> factory) {
        FACTORIES.put(clazz, () -> SINGLETONS.computeIfAbsent(clazz, c -> factory.get()));
    }

    public static <T> T resolve(Class<T> clazz) {
        try {
            if (FACTORIES.containsKey(clazz)) {
                return clazz.cast(FACTORIES.get(clazz).get());
            }
            T instance = createInstance(clazz);
            injectFields(instance);
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to resolve:", ex);
        }
    }

    private static <T> T createInstance(Class<T> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (var cons : constructors) {
            if (cons.getParameterCount() == 0) {
                return clazz.cast(cons.newInstance());
            } else {
                Object[] params = new Object[cons.getParameterCount()];
                Class<?>[] types = cons.getParameterTypes();
                for (int i = 0; i < types.length; i++) {
                    params[i] = resolve(types[i]);
                }
                return clazz.cast(cons.newInstance(params));
            }
        }
        throw new RuntimeException("No suitable constructor found for: " + clazz.getName());
    }

    private static <T> void injectFields(T instance) throws Exception {
        for (Field f : instance.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(Inject.class))
                continue;
            f.setAccessible(true);
            f.set(instance, resolve(f.getType()));
        }
    }

}
