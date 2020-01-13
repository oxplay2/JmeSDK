package com.jayfella.sdk.component.builder;

import com.jayfella.sdk.component.Component;

import java.util.List;

public interface ComponentSetBuilder<T> {

    void setObject(T object);
    void setObject(T object, String... ignoredProperties);
    List<Component> build();

}
