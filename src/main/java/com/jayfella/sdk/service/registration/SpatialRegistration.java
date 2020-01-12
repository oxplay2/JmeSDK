package com.jayfella.sdk.service.registration;

import com.jayfella.sdk.ext.registrar.ClassRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.NoArgsSpatialRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.registrar.ParticleEmitterSpatialRegistrar;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.HashSet;
import java.util.Set;

public class SpatialRegistration {

    private Set<SpatialRegistrar> registeredSpatials = new HashSet<>() {
    };

    public SpatialRegistration() {

        registerSpatial(NoArgsSpatialRegistrar.create(Node.class));
        registerSpatial(new ParticleEmitterSpatialRegistrar());
    }

    public void registerSpatial(SpatialRegistrar spatialRegistrar) {

        Class<? extends Spatial> classToRegister = spatialRegistrar.getRegisteredClass();

        Class<? extends Spatial> existingClass = registeredSpatials.stream()
                .filter(registrar -> registrar.getRegisteredClass().getName().equals(classToRegister.getName()))
                .findFirst()
                .map(ClassRegistrar::getRegisteredClass)
                .orElse(null);

        if (existingClass == null) {
            registeredSpatials.add(spatialRegistrar);
        }
    }

    public Set<SpatialRegistrar> getRegisteredSpatials() {
        return registeredSpatials;
    }
}
