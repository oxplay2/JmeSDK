package com.jayfella.sdk.component.reflection;

import com.jayfella.sdk.component.Component;
import com.jayfella.sdk.core.ThreadRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectedProperty {

    private final Object parent;

    private final Method getter;
    private final Method setter;
    private final Component component;

    public ReflectedProperty(Object parent, Method getter, Method setter, Component component) {
        this.parent = parent;
        this.getter = getter;
        this.setter = setter;
        this.component = component;

        component.setPropertyChangedEvent(value -> {

            ThreadRunner.runInJmeThread(() -> {
                try {
                    setter.invoke(parent, value);

                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });

        });

    }

    public void update() {
        try {
            Object value = getter.invoke(parent);
            this.component.setValue(value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setValue(Object value) {
        try {
            setter.invoke(parent, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getValue() {
        try {
            return getter.invoke(parent);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
