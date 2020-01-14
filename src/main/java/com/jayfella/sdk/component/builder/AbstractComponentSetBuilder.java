package com.jayfella.sdk.component.builder;

import com.jayfella.sdk.component.Component;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.Collection;

public abstract class AbstractComponentSetBuilder<T> implements ComponentSetBuilder<T> {

    protected TitledPane createTitledPane(String title, Collection<Component> components) {
        return createTitledPane(title, components.toArray(new Component[0]));
    }

    protected TitledPane createTitledPane(String title, Component... components) {

        TitledPane titledPane = new TitledPane();
        titledPane.setText(title);

        VBox vBox = new VBox();
        vBox.setMinWidth(150);
        ScrollPane scrollPane = new ScrollPane();
        // scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBox);

        vBox.getChildren().addAll(components);

        titledPane.setContent(scrollPane);

        return titledPane;
    }

}
