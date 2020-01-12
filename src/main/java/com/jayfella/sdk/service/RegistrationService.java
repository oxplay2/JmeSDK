package com.jayfella.sdk.service;

import com.jayfella.sdk.core.ExternalClassLoader;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.ext.registrar.control.ControlRegistrar;
import com.jayfella.sdk.ext.registrar.filter.FilterRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.service.registration.ControlRegistration;
import com.jayfella.sdk.service.registration.FilterRegistration;
import com.jayfella.sdk.service.registration.SpatialRegistration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class RegistrationService implements Service {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final FilterRegistration filterRegistration = new FilterRegistration();
    private final SpatialRegistration spatialRegistration = new SpatialRegistration();
    private final ControlRegistration controlRegistration = new ControlRegistration();

    public RegistrationService() {

    }

    public FilterRegistration getFilterRegistration() {
        return filterRegistration;
    }

    public SpatialRegistration getSpatialRegistration() {
        return spatialRegistration;
    }

    public ControlRegistration getControlRegistration() {
        return controlRegistration;
    }

    @Override
    public void stopService() {

    }

    private void procesFilterRegistrations() {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new SubTypesScanner());

        Reflections reflections = new Reflections(builder);

        Set<Class<? extends FilterRegistrar>> classes = new HashSet<>();

        try {
            classes = reflections.getSubTypesOf(FilterRegistrar.class);
        }
        catch (Exception ex) {
            // ignore
        }

        FilterRegistration registration = ServiceManager.getService(RegistrationService.class).getFilterRegistration();

        for (Class<? extends FilterRegistrar> clazz : classes) {

            try {
                Constructor<? extends FilterRegistrar> constructor = clazz.getConstructor();
                FilterRegistrar registrar = constructor.newInstance();

                if (registrar.getRegisteredClass() == null) {
                    continue;
                }

                registration.registerFilter(registrar);
                log.info("Registering Filter: " + registrar.getRegisteredClass().getName());

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    private void procesSpatialRegistrations() {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new SubTypesScanner());

        Reflections reflections = new Reflections(builder);

        Set<Class<? extends SpatialRegistrar>> classes = new HashSet<>();

        try {
            classes = reflections.getSubTypesOf(SpatialRegistrar.class);
        }
        catch (Exception ex) {
            // ignore
        }

        // Sort them alphabetically
        // List<Class<? extends AppState>> sortedClasses = new ArrayList<>(classes);
        // sortedClasses.sort(Comparator.comparing(Class::getSimpleName));

        SpatialRegistration registration = ServiceManager.getService(RegistrationService.class).getSpatialRegistration();

        for (Class<? extends SpatialRegistrar> clazz : classes) {

            try {
                Constructor<? extends SpatialRegistrar> constructor = clazz.getConstructor();
                SpatialRegistrar registrar = constructor.newInstance();

                if (registrar.getRegisteredClass() == null) {
                    continue;
                }

                registration.registerSpatial(registrar);
                log.info("Registering Spatial: " + registrar.getRegisteredClass().getName());

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    private void processControlRegistrations() {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new SubTypesScanner());

        Reflections reflections = new Reflections(builder);

        Set<Class<? extends ControlRegistrar>> classes = new HashSet<>();

        try {
            classes = reflections.getSubTypesOf(ControlRegistrar.class);
        }
        catch (Exception ex) {
            // ignore
        }

        // Sort them alphabetically
        // List<Class<? extends AppState>> sortedClasses = new ArrayList<>(classes);
        // sortedClasses.sort(Comparator.comparing(Class::getSimpleName));

        ControlRegistration registration = ServiceManager.getService(RegistrationService.class).getControlRegistration();

        for (Class<? extends ControlRegistrar> clazz : classes) {

            try {
                Constructor<? extends ControlRegistrar> constructor = clazz.getConstructor();
                ControlRegistrar registrar = constructor.newInstance();

                if (registrar.getRegisteredClass() == null) {
                    continue;
                }

                registration.registerControl(registrar);
                log.info("Registering Control: " + registrar.getRegisteredClass().getName());

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    public void processRegistrations() {

        log.info("Processing Registrations.");

        procesFilterRegistrations();
        int filterCount = ServiceManager.getService(RegistrationService.class).getFilterRegistration().getRegisteredFilters().size();
        log.info(String.format("Filters Registered: %d", filterCount));

        procesSpatialRegistrations();
        int spatialCount = ServiceManager.getService(RegistrationService.class).getSpatialRegistration().getRegisteredSpatials().size();
        log.info(String.format("Spatials Registered: %d", spatialCount));

        processControlRegistrations();
        int controlCount = ServiceManager.getService(RegistrationService.class).getControlRegistration().getRegisteredControls().size();
        log.info(String.format("Controls Registered: %d", controlCount));


        log.info(String.format("Total Registrations: %d", filterCount + spatialCount + controlCount));

    }


}
