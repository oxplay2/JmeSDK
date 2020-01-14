package com.jayfella.sdk.service;

import com.jayfella.sdk.component.Component;
import com.jayfella.sdk.component.DisposableComponent;
import com.jayfella.sdk.component.builder.ComponentSetBuilder;
import com.jayfella.sdk.component.control.AnimComposerComponent;
import com.jme3.anim.AnimComposer;
import com.jme3.scene.control.Control;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays the properties of a given object in the properties window.
 * Properties are discovered using a ComponentSetBuilder.
 * @deprecated - please use InspectorService2.class
 */

@Deprecated
public class InspectorService implements Service {

    private static final Logger log = LoggerFactory.getLogger(InspectorService.class);

    // a list of classes that get their own TitledPane/Dropdown
    // for example a Material has its own dropdown.
    // A module cannot be registered as a component.
    private Map<Class<?>, ComponentSetBuilder<?>> moduleBuilders = new HashMap<>();

    // a list of classes that will override the default reflection builder.
    // for example an AnimComposer needs its own component.
    private Map<Class<? extends Control>, Class<? extends Component>> customComponents = new HashMap<>();

    private final Accordion accordion;

    public InspectorService(Accordion accordion) {
        this.accordion = accordion;

        //moduleBuilders.put(Material.class, new MaterialComponentBuilder());
        customComponents.put(AnimComposer.class, AnimComposerComponent.class);
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

        /*
        for (TitledPane pane : accordion.getPanes()) {
            disposeComponents(pane);
        }

        accordion.getPanes().clear();

        if (object == null) {
            return;
        }

        Class<? extends Component> classCustomComponent = customComponents.get(object.getClass());

        if (classCustomComponent != null) {

            Component component = null;

            try {
                Constructor<? extends Component> constructor = classCustomComponent.getConstructor();
                component = constructor.newInstance();
                // component.load();

            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                // e.printStackTrace();
            }

            if (component != null) {

                component.setPropertyName(object.getClass().getSimpleName());
                component.setValue(object);

                TitledPane titledPane = new TitledPane();
                titledPane.setText(object.getClass().getSimpleName());

                VBox vBox = new VBox();
                vBox.setMinWidth(150);
                ScrollPane scrollPane = new ScrollPane();
                // scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
                scrollPane.setContent(vBox);

                vBox.getChildren().add(component);

                titledPane.setContent(scrollPane);
                accordion.getPanes().add(titledPane);
            }

        }
        else {

            // Obtain a list of unique getters and setters
            ReflectedComponentBuilder<Object> reflectedComponentBuilder = new ReflectedComponentBuilder<>();
            reflectedComponentBuilder.setObject(object);

            // build a list of components to edit the getters/setters
            List<Component> components = reflectedComponentBuilder.build();

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
                    vBox.getChildren().add(component);
                }
                accordion.getPanes().add(titledPane);
            }


            // find all the getters/setters that match any registered modules and put them in their own TitledPane/DropDown.
            // and if one isn't found (and it's not registered as a component) - then display the regular reflected properties.

            // for each getter, if it's not a component
            // - check if it has a modulebuilder
            //      - if it does build it
            //      - if it doesn't, use the reflection builder.

            // process any modulebuilders

            for (Map.Entry<Class<?>, ComponentSetBuilder<?>> entry : moduleBuilders.entrySet()) {

                List<Method> methods = reflectedComponentBuilder.getReflectedProperties().getGetters().stream()
                        .filter(getter -> getter.getReturnType() == entry.getKey())
                        .collect(Collectors.toList());


                for (int i = 0; i < methods.size(); i++) {

                    Object obj;

                    try {
                        obj = methods.get(i).invoke(object);
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
                        vBox.getChildren().add(component);
                    }

                    accordion.getPanes().add(titledPane);
                    // titledPane.setExpanded(true);

                }

            }

            Map<Class<?>, Class<? extends Component>> componentClasses = reflectedComponentBuilder.getComponentClasses();

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
            List<Method> unregisteredGetters = reflectedComponentBuilder.getReflectedProperties().getGetters()
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

                ReflectedComponentBuilder<Object> unregisteredPropsBuilder = new ReflectedComponentBuilder<>();
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
                    vBox.getChildren().add(component);
                }

                accordion.getPanes().add(titledPane);

            }

        }

        // expand the first titled pane.
        // This is especially important if there's only one titled pane. It looks empty otherwise.
        if (!accordion.getPanes().isEmpty()) {
            accordion.getPanes().get(0).setExpanded(true);
        }


         */
    }

    @Override
    public void stopService() {


    }

}
