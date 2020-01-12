package com.jayfella.sdk.service.registration;

import com.jayfella.sdk.ext.registrar.ClassRegistrar;
import com.jayfella.sdk.ext.registrar.control.ControlRegistrar;
import com.jme3.scene.control.Control;

import java.util.HashSet;
import java.util.Set;

public class ControlRegistration {

    private Set<ControlRegistrar> registeredControls = new HashSet<>();

    public ControlRegistration() {

    }

    public void registerControl(ControlRegistrar controlRegistrar) {
        // registeredControls.add(registrar);

        Class<? extends Control> classToRegister = controlRegistrar.getRegisteredClass();

        Class<? extends Control> existingClass = registeredControls.stream()
                .filter(registrar -> registrar.getRegisteredClass().getName().equals(classToRegister.getName()))
                .findFirst()
                .map(ClassRegistrar::getRegisteredClass)
                .orElse(null);

        if (existingClass == null) {
            registeredControls.add(controlRegistrar);
        }

    }

    public Set<ControlRegistrar> getRegisteredControls() {
        return registeredControls;
    }
}
