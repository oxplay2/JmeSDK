package com.jayfella.sdk.service;

import com.jayfella.sdk.component.builder.impl.AnimComposetComponentSetBuilder;
import com.jayfella.sdk.component.builder.impl.SpatialComponentSetBuilder;
import com.jayfella.sdk.ext.component.Component;
import com.jayfella.sdk.ext.component.DisposableComponent;
import com.jayfella.sdk.ext.component.UpdatableComponent;
import com.jayfella.sdk.ext.component.builder.ComponentBuilder;
import com.jayfella.sdk.ext.component.builder.ComponentSetBuilder;
import com.jayfella.sdk.ext.component.builder.ReflectedComponentBuilder;
import com.jayfella.sdk.ext.core.Service;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.registrar.component.builder.ComponentSetBuilderRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.ext.service.RegistrationService;
import com.jme3.anim.AnimComposer;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class InspectorService2 implements Service {

    private final Accordion accordion;
    private final GridPane loadingIndicator;

    private final Timer timer = new Timer();
    private final long timerDelay = 1000L / 30L; // 30fps
    private final TimerTask timerTask;

    // private Object object;
    private final Map<Class<?>, ComponentSetBuilder<?>> componentSetBuilders = new HashMap<>();

    public InspectorService2(Accordion accordion, GridPane loadingIndicator) {
        this.accordion = accordion;
        this.loadingIndicator = loadingIndicator;

        this.loadingIndicator.setVisible(false);

        componentSetBuilders.put(Node.class, new SpatialComponentSetBuilder<Node>());
        componentSetBuilders.put(Geometry.class, new SpatialComponentSetBuilder<Geometry>());
        componentSetBuilders.put(AnimComposer.class, new AnimComposetComponentSetBuilder<AnimComposer>());

        timerTask = new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1L, timerDelay);
    }

    public void updateSpatialRegistrations() {

        RegistrationService registrationService = ServiceManager.getService(RegistrationService.class);
        Set<SpatialRegistrar> spatialRegistrars = registrationService.getSpatialRegistration().getRegistrations();

        // Register spatials to use the generic spatial builder first, and then let the user specify theirs specifically.
        // this means if they don't specify one, the default one will be used,
        // but if they do specify one, theirs will be used instead.
        for (SpatialRegistrar registrar : spatialRegistrars) {
            componentSetBuilders.put(registrar.getRegisteredClass(), new SpatialComponentSetBuilder<>());
        }

    }

    public void updateComponentSetBuilders() {

        RegistrationService registrationService = ServiceManager.getService(RegistrationService.class);
        Set<ComponentSetBuilderRegistrar> componentSetBuilderRegistrars = registrationService.getComponentSetBuilderRegistration().getRegistrations();

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        for (ComponentSetBuilderRegistrar registrar : componentSetBuilderRegistrars) {

            ComponentSetBuilder<?> builder = registrar.createInstance(engineService);

            componentSetBuilders.put(registrar.getPrimitiveClass(), builder);
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
            loadingIndicator.setVisible(true);

            CompletableFuture.supplyAsync(componentSetBuilder::build)
                    .thenAccept(titledPanes ->

                            ThreadRunner.runInJfxThread(() -> {

                                        accordion.getPanes().addAll(titledPanes);

                                        if (!accordion.getPanes().isEmpty()) {
                                            accordion.getPanes().get(0).setExpanded(true);
                                        }

                                        loadingIndicator.setVisible(false);

                                    }));

            // List<TitledPane> titledPanes = componentSetBuilder.build();
            // accordion.getPanes().addAll(titledPanes);

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
        timer.cancel();
        timer.purge();
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
