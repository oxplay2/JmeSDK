package com.jayfella.sdk.component.builder.impl;

import com.jayfella.sdk.component.*;
import com.jayfella.sdk.component.builder.ComponentSetBuilder;
import com.jayfella.sdk.component.builder.UniqueProperties;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniquePropertyBuilder<T> implements ComponentSetBuilder<T> {

    private final Map<Class<?>, Class<? extends Component>> componentClasses = new HashMap<>();
    private UniqueProperties uniqueProperties;

    public UniquePropertyBuilder() {

        componentClasses.put(boolean.class, BooleanComponent.class);
        componentClasses.put(ColorRGBA.class, ColorRgbaComponent.class);
        componentClasses.put(Enum.class, EnumComponent.class);
        componentClasses.put(float.class, FloatComponent.class);
        componentClasses.put(int.class, IntegerComponent.class);
        componentClasses.put(Material.class, MaterialComponent.class);
        componentClasses.put(String.class, StringComponent.class);
        componentClasses.put(Vector3f.class, Vector3fComponent.class);
        componentClasses.put(Vector4f.class, Vector4fComponent.class);
        componentClasses.put(Quaternion.class, QuaternionComponent.class);
    }

    public Map<Class<?>, Class<? extends Component>> getComponentClasses() {
        return componentClasses;
    }

    @Override
    public void setObject(Object object, String... ignoredProperties) {
        this.uniqueProperties = new UniqueProperties(object, ignoredProperties);
    }

    @Override
    public void setObject(Object object) {
        this.uniqueProperties = new UniqueProperties(object);
    }

    public UniqueProperties getUniqueProperties() {
        return uniqueProperties;
    }

    public List<Component> build() {

        List<Component> components = new ArrayList<>();

        for (Method getter : uniqueProperties.getGetters()) {

            Map.Entry<Class<?>, Class<? extends Component>> entry = componentClasses.entrySet().stream()
                    .filter(c -> getter.getReturnType() == c.getKey() || ( getter.getReturnType().isEnum() && c.getKey() == Enum.class ) )
                    .findFirst()
                    .orElse(null);

            if (entry != null) {

                Method setter = uniqueProperties.getSetters().stream()
                        .filter(s -> {
                            // s.getName().substring(3).equalsIgnoreCase(getter.getName().substring(3))

                            String getterSuffix = UniqueProperties.getSuffix(getter.getName());
                            String setterSuffix = UniqueProperties.getSuffix(s.getName());

                            return getterSuffix.equalsIgnoreCase(setterSuffix);
                        })
                        .findFirst()
                        .orElse(null);

                try {

                    Class<? extends Component> componentClass = entry.getValue();
                    Constructor<? extends Component> constructor = componentClass.getConstructor(Object.class, Method.class, Method.class);
                    Component component = constructor.newInstance(uniqueProperties.getObject(), getter, setter);
                    component.load();
                    // component.setPropertyName(getter.getName().substring(3));
                    component.setPropertyName(UniqueProperties.getSuffix(getter.getName()));


                    // we have a special case for enums
                    if (getter.getReturnType().isEnum()) {

                        Class<? extends Enum> values = (Class<? extends Enum>) getter.getReturnType();

                        EnumComponent enumComponent = (EnumComponent) component;
                        enumComponent.setEnumValues(values);
                    }
                    components.add(component);

                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

        }

        return components;

    }

}
