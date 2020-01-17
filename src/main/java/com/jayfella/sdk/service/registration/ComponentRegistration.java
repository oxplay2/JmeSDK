package com.jayfella.sdk.service.registration;

import com.jayfella.sdk.ext.component.Component;
import com.jayfella.sdk.ext.registrar.ClassRegistrar;
import com.jayfella.sdk.ext.registrar.component.ComponentRegistrar;

import java.util.HashSet;
import java.util.Set;

public class ComponentRegistration {

    private Set<ComponentRegistrar> registeredComponents = new HashSet<>();

    public void registerComponent(ComponentRegistrar componentRegistrar) {

        Class<? extends Component> classToRegister = componentRegistrar.getRegisteredClass();

        Class<? extends Component> existingClass = registeredComponents.stream()
                .filter(registrar -> registrar.getRegisteredClass().getName().equals(classToRegister.getName()))
                .findFirst()
                .map(ClassRegistrar::getRegisteredClass)
                .orElse(null);

        if (existingClass == null) {
            registeredComponents.add(componentRegistrar);
        }
    }

    public Set<ComponentRegistrar> getRegisteredComponents() {
        return registeredComponents;
    }
}
