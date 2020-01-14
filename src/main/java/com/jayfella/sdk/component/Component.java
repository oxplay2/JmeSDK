package com.jayfella.sdk.component;

import com.jayfella.sdk.component.reflection.ReflectedProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class Component extends VBox implements Initializable {

    private String propertyName = "";

    private ReflectedProperty reflectedProperty;
    private PropertyChangedEvent propertyChangedEvent;

    public Component() {
        this(null, null, null);
    }

    public Component(Object parent, Method getter, Method setter) {
        if (parent != null) {
            this.reflectedProperty = new ReflectedProperty(parent, getter, setter, this);
        }

        load();
    }

    protected void load(String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void load();

    public ReflectedProperty getReflectedProperty() {
        return reflectedProperty;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String name) {
        this.propertyName = name;
    }

    public void setValue(Object value) {
        if (propertyChangedEvent != null) {
            propertyChangedEvent.propertyChanged(value);
        }
    }

    public PropertyChangedEvent getPropertyChangedEvent() {
        return this.propertyChangedEvent;
    }

    public void setPropertyChangedEvent(PropertyChangedEvent event) {
        this.propertyChangedEvent = event;
    }

}
