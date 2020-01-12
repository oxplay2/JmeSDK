package com.jayfella.sdk.service.registration;

import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.core.ThreadRunner;
import com.jayfella.sdk.ext.registrar.ClassRegistrar;
import com.jayfella.sdk.ext.registrar.filter.FilterRegistrar;
import com.jayfella.sdk.ext.registrar.filter.NoArgsFilterRegistrar;
import com.jayfella.sdk.registrar.DirectionalLightShadowFilterRegistrar;
import com.jayfella.sdk.service.JmeEngineService;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.*;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.water.WaterFilter;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.Map;

public class FilterRegistration {

    // An ordered map. Allows us to move filters up and down in the order they need to be to work properly.
    // for example shadows need to be before bloom. ToneMap should be last, etc.
    private final ListOrderedMap<FilterRegistrar, Filter> registeredFilters = new ListOrderedMap<>();

    public FilterRegistration() {

        // The internal post-processors are in the order the should be added.
        registerFilter(new DirectionalLightShadowFilterRegistrar());
        registerFilter(NoArgsFilterRegistrar.create(SSAOFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(BloomFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(CartoonEdgeFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(ColorOverlayFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(ComposeFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(CrossHatchFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(DepthOfFieldFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(FadeFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(FogFilter.class));
        // registerFilter(NoArgsFilterRegistrar.create(LightScatteringFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(PosterizationFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(RadialBlurFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(WaterFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(ToneMapFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(FXAAFilter.class));
        registerFilter(NoArgsFilterRegistrar.create(TranslucentBucketFilter.class));

        // We can't know the order of external post-processors, so we provide them user with a sortable
        // list that allows them to move filters up and down.

    }

    // Register a filter as available.
    // This does not mean it's enabled or used, it just means it's added to the list of available filters.
    public void registerFilter(FilterRegistrar filterRegistrar) {

        // check whether this filter has already been registered or not.

        Class<? extends Filter> classToRegister = filterRegistrar.getRegisteredClass();

        Class<? extends Filter> existingClass = registeredFilters.keySet().stream()
                .filter(registrar -> registrar.getRegisteredClass().getName().equals(classToRegister.getName()))
                .findFirst()
                .map(ClassRegistrar::getRegisteredClass)
                .orElse(null);

        if (existingClass == null) {
            registeredFilters.put(filterRegistrar, null);
        }
    }

    private void getOrCreateFilter(Class<? extends Filter> filterClass) {

        FilterRegistrar registrar = registeredFilters.keySet().stream()
                .filter(filter -> filter.getRegisteredClass().getName().equals(filterClass.getName()))
                .findFirst()
                .orElse(null);

        if (registrar != null) {

            Filter filter = registeredFilters.get(registrar);

            if (filter != null) {
                return;
            }

            filter = registrar.createInstance(ServiceManager.getService(JmeEngineService.class));
            registeredFilters.put(registrar, filter);

        }

    }

    // Filters are enabled and disabled but not refreshed.
    // This avoids multiple refreshing when more than one filter state is changed.
    // Call .refreshFilters() when you're done.
    public void setEnabled(Class<? extends Filter> filterClass, boolean enabled) {

        FilterRegistrar registrar = registeredFilters.keySet().stream()
                .filter(filter -> filter.getRegisteredClass().equals(filterClass))
                .findFirst()
                .orElse(null);

        if (registrar == null) {
            return;
        }

        Filter filter = registeredFilters.get(registrar);

        if (enabled) {
            if (filter == null) {
                getOrCreateFilter(filterClass);
            }
        }
        else {

            if (filter != null) {
                registeredFilters.put(registrar, null);
            }

        }
    }

    public FilterRegistrar getFilterRegistrar(Class<? extends Filter> filterClass) {
        return registeredFilters.entrySet().stream()
                .filter(entry -> entry.getKey().getRegisteredClass().equals(filterClass))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Filter getFilterInstance(Class<? extends Filter> filterClass) {
        return registeredFilters.entrySet().stream()
                .filter(entry -> entry.getKey().getRegisteredClass().equals(filterClass))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    // Create a new FilterPostProcessor, adds all enabled filters, and adds them to the 3D viewport.
    // See JmeEngineService.setFilterPostProcessor(fpp);
    public void refreshFilters() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        FilterPostProcessor fpp = new FilterPostProcessor(engineService.getAssetManager());

        int samples = engineService.getContext().getSettings().getSamples();
        fpp.setNumSamples(samples);

        for (Map.Entry<FilterRegistrar, Filter> entry : registeredFilters.entrySet()) {

            FilterRegistrar registrar = entry.getKey();
            Filter filter  = entry.getValue();

            // if the filter is null, it's not enabled.
            if (filter != null) {

                // have the requirements been met to allow this filter to work?
                // for example a shadow filter needs a light.
                boolean acceptable = registrar.sceneLoaded(filter, ServiceManager.getService(JmeEngineService.class).getRootNode());

                // if the requirements are acceptable and it's not enabled, enable it.
                if (acceptable) {
                    fpp.addFilter(filter);
                }
            }

        }

        ThreadRunner.runInJmeThread(() -> engineService.setFilterPostProcessor(fpp) );

    }

    public ListOrderedMap<FilterRegistrar, Filter> getRegisteredFilters() {
        return registeredFilters;
    }

}
