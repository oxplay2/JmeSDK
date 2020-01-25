package com.jayfella.sdk.service.impl;

import com.jayfella.sdk.ext.component.*;
import com.jayfella.sdk.ext.core.ExternalClassLoader;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.registrar.ClassRegistrar;
import com.jayfella.sdk.ext.registrar.Registrar;
import com.jayfella.sdk.ext.registrar.component.ComponentRegistrar;
import com.jayfella.sdk.ext.registrar.component.NoArgsComponentRegistrar;
import com.jayfella.sdk.ext.registrar.component.builder.ComponentBuilderRegistrar;
import com.jayfella.sdk.ext.registrar.component.builder.ComponentSetBuilderRegistrar;
import com.jayfella.sdk.ext.registrar.control.ControlRegistrar;
import com.jayfella.sdk.ext.registrar.control.NoArgsControlRegistrar;
import com.jayfella.sdk.ext.registrar.filter.FilterRegistrar;
import com.jayfella.sdk.ext.registrar.filter.NoArgsFilterRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.NoArgsSpatialRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.ext.registrar.startup.DependencyStartupRegistrar;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.ext.service.ProjectInjectorService;
import com.jayfella.sdk.ext.service.RegistrationService;
import com.jayfella.sdk.registrar.DirectionalLightShadowFilterRegistrar;
import com.jayfella.sdk.registrar.ParticleEmitterSpatialRegistrar;
import com.jayfella.sdk.registrar.TranslucentBucketFilterRegistrar;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.filters.*;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.water.WaterFilter;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class RegistrationServiceImpl extends RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final Registrar<FilterRegistrar> filterRegistration = new Registrar<>(FilterRegistrar.class);
    private final Registrar<SpatialRegistrar> spatialRegistration = new Registrar<>(SpatialRegistrar.class);
    private final Registrar<ControlRegistrar> controlRegistration = new Registrar<>(ControlRegistrar.class);
    private final Registrar<ComponentRegistrar> componentRegistration = new Registrar<>(ComponentRegistrar.class);

    private final Registrar<ComponentBuilderRegistrar> componentBuilderRegistration = new Registrar<>(ComponentBuilderRegistrar.class);
    private final Registrar<ComponentSetBuilderRegistrar> componentSetBuilderRegistration = new Registrar<>(ComponentSetBuilderRegistrar.class);

    private final Registrar<DependencyStartupRegistrar> dependencyStartupRegistration = new Registrar<>(DependencyStartupRegistrar.class);

    public RegistrationServiceImpl() {

        // register internal stuff.

        // filters
        filterRegistration.register(new DirectionalLightShadowFilterRegistrar());
        filterRegistration.register(NoArgsFilterRegistrar.create(SSAOFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(BloomFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(CartoonEdgeFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(ColorOverlayFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(ComposeFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(CrossHatchFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(DepthOfFieldFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(FadeFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(FogFilter.class));
        // regisfilterRegistration.registerterFilter(NoArgsFilterRegistrar.create(LightScatteringFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(PosterizationFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(RadialBlurFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(WaterFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(ToneMapFilter.class));
        filterRegistration.register(NoArgsFilterRegistrar.create(FXAAFilter.class));
        // filterRegistration.register(NoArgsFilterRegistrar.create(TranslucentBucketFilter.class));
        filterRegistration.register(new TranslucentBucketFilterRegistrar());

        // spatials
        spatialRegistration.register(NoArgsSpatialRegistrar.create(Node.class));
        spatialRegistration.register(new ParticleEmitterSpatialRegistrar());

        // controls
        controlRegistration.register(NoArgsControlRegistrar.create(BillboardControl.class));

        // components
        componentRegistration.register(NoArgsComponentRegistrar.create(boolean.class, BooleanComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(ColorRGBA.class, ColorRgbaComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(Enum.class, EnumComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(float.class, FloatComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(int.class, IntegerComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(Material.class, MaterialComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(String.class, StringComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(Vector3f.class, Vector3fComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(Vector4f.class, Vector4fComponent.class));
        componentRegistration.register(NoArgsComponentRegistrar.create(Quaternion.class, QuaternionComponent.class));

    }

    @Override
    public Registrar<FilterRegistrar> getFilterRegistration() {
        return filterRegistration;
    }

    @Override
    public Registrar<SpatialRegistrar> getSpatialRegistration() {
        return spatialRegistration;
    }

    @Override
    public Registrar<ControlRegistrar> getControlRegistration() {
        return controlRegistration;
    }

    @Override
    public Registrar<ComponentRegistrar> getComponentRegistration() {
        return componentRegistration;
    }

    @Override
    public Registrar<ComponentBuilderRegistrar> getComponentBuilderRegistration() {
        return componentBuilderRegistration;
    }

    @Override
    public Registrar<ComponentSetBuilderRegistrar> getComponentSetBuilderRegistration() {
        return componentSetBuilderRegistration;
    }

    @Override
    public Registrar<DependencyStartupRegistrar> getDependencyStartupRegistration() {
        return dependencyStartupRegistration;
    }

    @Override
    public void stopService() {

    }

    private void processRegistrar(Registrar registrar) {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new SubTypesScanner());

        Reflections reflections = new Reflections(builder);

        Set<?> classes = new HashSet<>();

        try {
            classes = reflections.getSubTypesOf(registrar.getClassType());
        }
        catch (Exception ex) {
            // ignore
        }

        for (Object obj : classes) {

            Class<? extends ClassRegistrar<?>> clazz = (Class<? extends ClassRegistrar<?>>) obj;

            try {
                Constructor<?> constructor = clazz.getConstructor();
                // FilterRegistrar registrar = constructor.newInstance();
                ClassRegistrar<?> classRegistrar = (ClassRegistrar<?>) constructor.newInstance();

                if (classRegistrar.getRegisteredClass() == null) {
                    continue;
                }

                // registration.registerFilter(registrar);
                registrar.register(classRegistrar);
                log.info("Registering: " + classRegistrar.getRegisteredClass().getName());

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void executeStartupRegistrations() {

        Set<DependencyStartupRegistrar> registrars = dependencyStartupRegistration.getRegistrations();

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        for (DependencyStartupRegistrar registrar : registrars) {

            registrar.createInstance(engineService);

        }

    }

    /*
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

        FilterRegistration registration = getFilterRegistration();

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

        SpatialRegistration registration = getSpatialRegistration();

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

        ControlRegistration registration = getControlRegistration();

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

    private void processComponentRegistrations() {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new SubTypesScanner());

        Reflections reflections = new Reflections(builder);

        Set<Class<? extends ComponentRegistrar>> classes = new HashSet<>();

        try {
            classes = reflections.getSubTypesOf(ComponentRegistrar.class);
        }
        catch (Exception ex) {
            // ignore
        }

        // Sort them alphabetically
        // List<Class<? extends AppState>> sortedClasses = new ArrayList<>(classes);
        // sortedClasses.sort(Comparator.comparing(Class::getSimpleName));

        ComponentRegistration registration = getComponentRegistration();

        for (Class<? extends ComponentRegistrar> clazz : classes) {

            try {
                Constructor<? extends ComponentRegistrar> constructor = clazz.getConstructor();
                ComponentRegistrar registrar = constructor.newInstance();

                if (registrar.getRegisteredClass() == null) {
                    continue;
                }

                log.info("Registering Component: " + registrar.getRegisteredClass().getName());
                registration.registerComponent(registrar);

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    */

    public void processRegistrations() {

        log.info("Processing Registrations.");

        processRegistrar(filterRegistration);
        int filterCount = getFilterRegistration().getRegistrations().size();
        log.info(String.format("Filters Registered: %d", filterCount));

        processRegistrar(spatialRegistration);
        int spatialCount = getSpatialRegistration().getRegistrations().size();
        log.info(String.format("Spatials Registered: %d", spatialCount));

        processRegistrar(controlRegistration);
        int controlCount = getControlRegistration().getRegistrations().size();
        log.info(String.format("Controls Registered: %d", controlCount));

        processRegistrar(componentRegistration);
        int componentCount = getComponentRegistration().getRegistrations().size();
        log.info(String.format("Components Registered: %d", controlCount));

        processRegistrar(componentBuilderRegistration);
        int componentBuilderCount = getComponentBuilderRegistration().getRegistrations().size();
        log.info(String.format("Component Builders Registered: %d", componentBuilderCount));

        processRegistrar(componentSetBuilderRegistration);
        int componentSetBuilderCount = getComponentSetBuilderRegistration().getRegistrations().size();
        log.info(String.format("ComponentSet Builders Registered: %d", componentSetBuilderCount));

        processRegistrar(dependencyStartupRegistration);
        int startupCount = getDependencyStartupRegistration().getRegistrations().size();
        log.info(String.format("Startup Actions Registered: %d", startupCount));

        log.info(String.format("Total Registrations: %d",
                filterCount +
                        spatialCount +
                        controlCount +
                        componentCount +
                        componentBuilderCount +
                        componentSetBuilderCount +
                        startupCount
        ));

    }

}
