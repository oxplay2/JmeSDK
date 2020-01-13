package com.jayfella.sdk.service;

import com.jayfella.sdk.component.Component;
import com.jayfella.sdk.component.DisposableComponent;
import com.jayfella.sdk.component.builder.ComponentSetBuilder;
import com.jayfella.sdk.component.builder.UniqueProperties;
import com.jayfella.sdk.component.builder.impl.MaterialPropertyBuilder;
import com.jayfella.sdk.component.builder.impl.UniquePropertyBuilder;
import com.jayfella.sdk.component.control.AnimComposerComponent;
import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetKey;
import com.jme3.bounding.BoundingVolume;
import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Displays the properties of a given object in the properties window.
 * Properties are discovered using a ComponentSetBuilder.
 */
public class InspectorService implements Service {

    private static final Logger log = LoggerFactory.getLogger(InspectorService.class);

    // a list of classes that get their own TitledPane/Dropdown
    // for example a Material has its own dropdown.
    // A module cannot be registered as a component.
    private Map<Class<?>, ComponentSetBuilder<?>> moduleBuilders = new HashMap<>();
    private Map<Class<? extends Control>, Class<? extends Component>> controlComponents = new HashMap<>();

    private final Accordion accordion;
    // private final UniquePropertyBuilder inspectorBuilder;

    public InspectorService(Accordion accordion) {
        this.accordion = accordion;

        moduleBuilders.put(Material.class, new MaterialPropertyBuilder());
        controlComponents.put(AnimComposer.class, AnimComposerComponent.class);
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

    public void setObject(Object object) {

        for (TitledPane pane : accordion.getPanes()) {
            disposeComponents(pane);
        }

        accordion.getPanes().clear();

        if (object == null) {
            return;
        }

        // Obtain a list of unique getters and setters
        UniquePropertyBuilder<Object> uniquePropertyBuilder = new UniquePropertyBuilder<>();
        uniquePropertyBuilder.setObject(object);

        // build a list of components to edit the getters/setters
        List<Component> components = uniquePropertyBuilder.build();

        if (!components.isEmpty()) {

            TitledPane titledPane = new TitledPane();
            titledPane.setText(object.getClass().getSimpleName());

            VBox vBox = new VBox();
            vBox.setMinWidth(150);
            ScrollPane scrollPane = new ScrollPane();
            // scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(vBox);

            titledPane.setContent(scrollPane);

            for (Component component : components) {
                vBox.getChildren().add(component.getJfxControl());
            }

            accordion.getPanes().add(titledPane);
            titledPane.setExpanded(true);
        }

        // if this is a spatial, deal with the controls.
        if (object instanceof Spatial) {

            Spatial spatial = (Spatial) object;

            List<Control> controls = new ArrayList<>();

            for (int i = 0; i < spatial.getNumControls(); i++) {
                controls.add(spatial.getControl(i));
            }

            for (Control control : controls) {

                TitledPane titledPane = new TitledPane();
                titledPane.setText(control.getClass().getSimpleName());

                VBox vBox = new VBox();
                vBox.setMinWidth(150);
                ScrollPane scrollPane = new ScrollPane();
                // scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
                scrollPane.setContent(vBox);

                titledPane.setContent(scrollPane);


                Class<? extends Component> componentClass = controlComponents.get(control.getClass());

                if (componentClass != null) {

                    try {
                        Constructor<? extends Component> constructor = componentClass.getConstructor();
                        Component component = constructor.newInstance();
                        component.load();

                        component.setPropertyName(spatial.getName());
                        component.setValue(control);

                        vBox.getChildren().addAll(component.getJfxControl());


                    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                        // e.printStackTrace();
                    }

                }
                else {

                    UniqueProperties uniqueProperties = new UniqueProperties(control);
                    UniquePropertyBuilder<Control> controlBuilder = new UniquePropertyBuilder<>();
                    controlBuilder.setObject(uniqueProperties);

                    List<Component> controlComponents = controlBuilder.build();

                    for (Component component : controlComponents) {
                        vBox.getChildren().add(component.getJfxControl());
                    }

                }

                accordion.getPanes().add(titledPane);

            }

        }

        // find all the getters/setters that match any registered modules and put them in their own TitledPane/DropDown.
        // and if one isn't found (and it's not registered as a component) - then display the regular reflected properties.

        // for each getter, if it's not a component
        // - check if it has a modulebuilder
        //      - if it does build it
        //      - if it doesn't, use the reflection builder.

        // process any modulebuilders

        for (Map.Entry<Class<?>, ComponentSetBuilder<?>> entry : moduleBuilders.entrySet()) {

            List<Method> methods = uniquePropertyBuilder.getUniqueProperties().getGetters().stream()
                    .filter(getter -> getter.getReturnType() == entry.getKey())
                    .collect(Collectors.toList());

            if (!methods.isEmpty()) {

                Object obj;

                try {
                    obj = methods.get(0).invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // e.printStackTrace();
                    continue;
                }

                if (obj == null) {
                    continue;
                }

                ComponentSetBuilder componentSetBuilder = entry.getValue();
                componentSetBuilder.setObject(obj);

                TitledPane titledPane = new TitledPane();
                titledPane.setText(entry.getKey().getSimpleName());

                VBox vBox = new VBox();
                vBox.setMinWidth(150);
                ScrollPane scrollPane = new ScrollPane();
                // scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
                scrollPane.setContent(vBox);

                titledPane.setContent(scrollPane);

                List<Component> entryComponents = entry.getValue().build();

                for (Component component : entryComponents) {
                    vBox.getChildren().add(component.getJfxControl());
                }

                accordion.getPanes().add(titledPane);
                // titledPane.setExpanded(true);
            }
        }

        Map<Class<?>, Class<? extends Component>> componentClasses = uniquePropertyBuilder.getComponentClasses();


        // unwanted things
        List<Class<?>> unwantedClasses = new ArrayList<>();
        unwantedClasses.add(BoundingVolume.class); // this is auto-calculated. It should be visualized, not modified.
        unwantedClasses.add(Transform.class); // changing these values does nothing, so it's pointless having it in the GUI
        unwantedClasses.add(AssetKey.class); // we can't do anything with this.

        // get a list of getters that do not contain:
        // - registered components (vector3f, etc)
        // - registered modules
        // - enums because we deal with them already.
        // - unwanted classes
        List<Method> unregisteredGetters = uniquePropertyBuilder.getUniqueProperties().getGetters()
                .stream()
                .filter(getter -> !componentClasses.containsKey(getter.getReturnType()))
                .filter(getter -> !moduleBuilders.containsKey(getter.getReturnType()))
                .filter(getter -> !getter.getReturnType().isEnum())
                .filter(getter -> !unwantedClasses.contains(getter.getReturnType()))
                .collect(Collectors.toList());

        // we should be left with getters that are not components and do not have a builder associated with them.
        // We'll push them through the reflection builder.

        for (Method getter : unregisteredGetters) {

            Object obj;

            try {
                obj = getter.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // e.printStackTrace();
                continue;
            }

            if (obj == null) {
                continue;
            }

            UniquePropertyBuilder<Object> unregisteredPropsBuilder = new UniquePropertyBuilder<>();
            unregisteredPropsBuilder.setObject(obj);

            TitledPane titledPane = new TitledPane();
            titledPane.setText(getter.getReturnType().getSimpleName());

            VBox vBox = new VBox();
            vBox.setMinWidth(150);
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setContent(vBox);

            titledPane.setContent(scrollPane);

            List<Component> entryComponents = unregisteredPropsBuilder.build();

            for (Component component : entryComponents) {
                vBox.getChildren().add(component.getJfxControl());
            }

            accordion.getPanes().add(titledPane);

        }

    }

    @Override
    public void stopService() {


    }

}
