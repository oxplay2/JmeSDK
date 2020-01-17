package com.jayfella.sdk.component.builder.impl;

import com.jayfella.sdk.ext.component.AbstractComponentSetBuilder;
import com.jayfella.sdk.ext.component.Component;
import com.jayfella.sdk.ext.component.control.AnimComposerComponent;
import javafx.scene.control.TitledPane;

import java.util.ArrayList;
import java.util.List;

public class AnimComposetComponentSetBuilder<AnimComposer> extends AbstractComponentSetBuilder<AnimComposer> {

    private AnimComposer object;
    private String[] ignoredProperties = new String[0];

    @Override
    public void setObject(AnimComposer object) {
        this.object = object;
    }

    @Override
    public void setObject(AnimComposer object, String... ignoredProperties) {
        this.object = object;
        this.ignoredProperties = ignoredProperties;
    }

    @Override
    public List<TitledPane> build() {

        Component animComposerComponent = new AnimComposerComponent();
        animComposerComponent.setValue(object);
        animComposerComponent.setPropertyName(object.getClass().getSimpleName());

        TitledPane titledPane = new TitledPane("AnimComposer", animComposerComponent);

        List<TitledPane> titledPanes = new ArrayList<>();
        titledPanes.add(titledPane);
        return titledPanes;
    }

}
