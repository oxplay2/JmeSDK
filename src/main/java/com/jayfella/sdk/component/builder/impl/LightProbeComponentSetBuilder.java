package com.jayfella.sdk.component.builder.impl;

import com.jayfella.sdk.component.LightProbeAreaComponent;
import com.jayfella.sdk.ext.component.AbstractComponentSetBuilder;
import com.jayfella.sdk.ext.component.Component;
import com.jayfella.sdk.ext.component.builder.ReflectedComponentBuilder;
import com.jme3.light.LightProbe;
import javafx.scene.control.TitledPane;

import java.util.ArrayList;
import java.util.List;

public class LightProbeComponentSetBuilder<T> extends AbstractComponentSetBuilder<LightProbe> {

    private LightProbe object;
    private String[] ignoredProperties = new String[0];

    @Override
    public void setObject(LightProbe object) {
        this.object = object;
    }

    @Override
    public void setObject(LightProbe object, String... ignoredProperties) {
        this.object = object;
        this.ignoredProperties = ignoredProperties;

    }

    @Override
    public List<TitledPane> build() {

        ReflectedComponentBuilder<LightProbe> componentBuilder = new ReflectedComponentBuilder<>();
        componentBuilder.setObject(object, "areaType", "radius", "color", "frustumCheckNeeded", "intersectsFrustum", "ready");

        List<Component> components = componentBuilder.build();

        LightProbeAreaComponent areaComponent = new LightProbeAreaComponent(object);
        areaComponent.setValue(object);
        // components.add(areaComponent);

        TitledPane titledPane = createTitledPane("Light", components);
        TitledPane areaTitledPane = createTitledPane("Area", areaComponent);

        List<TitledPane> titledPanes = new ArrayList<>();
        titledPanes.add(titledPane);
        titledPanes.add(areaTitledPane);

        return titledPanes;
    }

}
