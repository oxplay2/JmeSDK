package com.jayfella.sdk.core;

import com.jayfella.sdk.service.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * The service manager holds single instances of service such as JmeEngineService.
 * Allows us to easily access these services from various areas of the SDK without having to pass references.
 */
public class ServiceManager {

    private static Map<Class<? extends Service>, Service> services = new HashMap<>();

    public static <T extends Service> T getService(Class<T> serviceClass) {
        return (T)services.get(serviceClass);
    }

    public static <T extends Service> T registerService(Class<T> serviceClass) {

        try {
            Constructor<T> constructor = serviceClass.getConstructor();
            T service = constructor.newInstance();
            services.put(serviceClass, service);
            return service;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends Service> T registerService(Service service) {
        return (T) services.put(service.getClass(), service);
    }

    public static void stop() {
        services.values().forEach(Service::stopService);
    }

}
