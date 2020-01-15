package com.jayfella.sdk.service;

import com.jayfella.sdk.component.Component;
import com.jayfella.sdk.component.DisposableComponent;
import com.jayfella.sdk.component.UpdatableComponent;
import com.jayfella.sdk.component.builder.ComponentBuilder;
import com.jayfella.sdk.component.builder.ComponentSetBuilder;
import com.jayfella.sdk.component.builder.impl.AnimComposetComponentSetBuilder;
import com.jayfella.sdk.component.builder.impl.ReflectedComponentBuilder;
import com.jayfella.sdk.component.builder.impl.SpatialComponentSetBuilder;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jme3.anim.AnimComposer;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.*;

public class InspectorService2 implements Service {

    private final Accordion accordion;

    // private Object object;
    private final Map<Class<?>, ComponentSetBuilder<?>> componentSetBuilders = new HashMap<>();

    public InspectorService2(Accordion accordion) {
        this.accordion = accordion;

        componentSetBuilders.put(Node.class, new SpatialComponentSetBuilder<Node>());
        componentSetBuilders.put(Geometry.class, new SpatialComponentSetBuilder<Geometry>());
        componentSetBuilders.put(AnimComposer.class, new AnimComposetComponentSetBuilder<AnimComposer>());
    }

    public void updateSpatialRegistrations() {

        RegistrationService registrationService = ServiceManager.getService(RegistrationService.class);
        Set<SpatialRegistrar> spatialRegistrars = registrationService.getSpatialRegistration().getRegisteredSpatials();

        for (SpatialRegistrar registrar : spatialRegistrars) {
            componentSetBuilders.put(registrar.getRegisteredClass(), new SpatialComponentSetBuilder<>());
        }

    }

    private TitledPane createTitledPane(String title, Collection<Component> components) {
        return createTitledPane(title, components.toArray(new Component[0]));
    }

    private TitledPane createTitledPane(String title, Component... components) {

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

    /*
        Things that are going to be pushed are:
        - Spatials
            - Node
            - Geometry
        - Controls
        - Lights
     */
    public void setObject(Object object) {

        for (TitledPane pane : accordion.getPanes()) {
            disposeComponents(pane);
        }

        accordion.getPanes().clear();

        if (object == null) {
            return;
        }

        ComponentSetBuilder componentSetBuilder = componentSetBuilders.get(object.getClass());

        if (componentSetBuilder != null) {

            componentSetBuilder.setObject(object);

            List<TitledPane> titledPanes = componentSetBuilder.build();
            accordion.getPanes().addAll(titledPanes);
        }

        else {

            ComponentBuilder<Object> componentBuilder = new ReflectedComponentBuilder();
            componentBuilder.setObject(object);

            List<Component> components = componentBuilder.build();

            TitledPane titledPane = createTitledPane("Properties", components);
            accordion.getPanes().add(titledPane);
        }


        // expand the first titled pane.
        // This is especially important if there's only one titled pane. It looks empty otherwise.
        if (!accordion.getPanes().isEmpty()) {
            accordion.getPanes().get(0).setExpanded(true);
        }

    }

    public void refresh() {
        recurse(accordion);
    }

    private void recurse(javafx.scene.Node node) {

        if (node instanceof UpdatableComponent) {
            UpdatableComponent updatableComponent = (UpdatableComponent) node;
            updatableComponent.update();
        }

        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            parent.getChildrenUnmodifiable().forEach(this::recurse);
        }

    }

    @Override
    public void stopService() {

    }

    /**
     * Calls dispose on components that require it (e.g. AnimComposerComponent needs to reset itself).
     * @param node the node to dispose.
     */
    private void disposeComponents(javafx.scene.Node node) {

        if (node instanceof DisposableComponent) {
            DisposableComponent disposableComponent = (DisposableComponent) node;
            disposableComponent.dispose();
        }

        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            parent.getChildrenUnmodifiable().forEach(this::disposeComponents);
        }

    }

}
