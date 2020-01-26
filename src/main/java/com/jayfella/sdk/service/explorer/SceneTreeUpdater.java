package com.jayfella.sdk.service.explorer;

import com.jayfella.sdk.sdk.tree.scene.ControlTreeItem;
import com.jayfella.sdk.sdk.tree.scene.GeometryTreeItem;
import com.jayfella.sdk.sdk.tree.scene.LightTreeItem;
import com.jayfella.sdk.sdk.tree.scene.NodeTreeItem;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Updates a Scene TreeView to match the scene. Generally called after the scene has been modified.
 *
 * If we add items via the SDK we can add treeItems manually to the treeView.
 * But if the SDK does not add items (maybe an appstate add them?), it does not know they have been added.
 * This updater overcomes that issue by comparing the TreeView with the scene, and updating it to represent the given scene.
 *
 * - Will add spatials, controls and lights that are in the scene but not in the treeview.
 * - Will remove spatials, controls and lights that are in the treeview but not in the scene.
 * - Will therefore return a representation of the given scene.
 */
public class SceneTreeUpdater {

    public SceneTreeUpdater() {
    }

    public void refresh(TreeItem<Object> treeItem, Spatial scene) {

        // iterate over each item and check whether it exists.
        // - if it exists in the scene, but not in the tree, add it.
        // - if it exists in the tree, but not in the scene delete it.

        traverseLights(treeItem, scene);
        traverseControls(treeItem, scene);
        traverseScene(treeItem, scene);
    }

    private void traverseScene(TreeItem<Object> treeItem, Spatial sceneItem) {

        // we only need to traverse children, so only accept Nodes.

        if (sceneItem instanceof Node) {

            Node node = (Node) sceneItem;

            ObservableList<TreeItem<Object>> treeChildren = treeItem.getChildren();
            List<Spatial> nodeChildren = node.getChildren();

            // check if each child exists in the tree
            nodeChildren.forEach(childSpatial -> {

                TreeItem<Object> existingTreeItem = treeChildren.stream()
                        .filter(treeChild -> treeChild.getValue() == childSpatial)
                        .findFirst()
                        .orElse(null);

                // if it doesnt exist in the tree, add it.
                if (existingTreeItem == null) {

                    if (childSpatial instanceof Node) {

                        Node childNode = (Node) childSpatial;

                        NodeTreeItem nodeTreeItem = new NodeTreeItem(childNode);
                        treeItem.getChildren().add(nodeTreeItem);

                        traverseScene(nodeTreeItem, childNode);

                        traverseLights(nodeTreeItem, childNode);
                        traverseControls(nodeTreeItem, childNode);

                    }
                    else if (childSpatial instanceof Geometry) {

                        Geometry childGeometry = (Geometry) childSpatial;

                        GeometryTreeItem geometryTreeItem = new GeometryTreeItem(childGeometry);
                        treeItem.getChildren().add(geometryTreeItem);

                        traverseLights(geometryTreeItem, childGeometry);
                        traverseControls(geometryTreeItem, childGeometry);

                    }

                }
                else {

                    traverseScene(existingTreeItem, childSpatial);

                    traverseLights(existingTreeItem, childSpatial);
                    traverseControls(existingTreeItem, childSpatial);

                }

                List<Light> lights = new ArrayList<>();
                List<Control> controls = new ArrayList<>();

                for (int i = 0; i < sceneItem.getLocalLightList().size(); i++) {
                    lights.add(sceneItem.getLocalLightList().get(i));
                }

                for (int i = 0; i < sceneItem.getNumControls(); i++) {
                    controls.add(sceneItem.getControl(i));
                }

                // if it exists in the tree, and not the scene, remove it.
                treeChildren.removeIf(childTreeItem -> {

                    if (treeItem.getValue() != null) {

                        if (childTreeItem.getValue() instanceof Spatial) {
                            return !nodeChildren.contains(childTreeItem.getValue());
                        }
                        else if (childTreeItem.getValue() instanceof Light) {
                            return !lights.contains(childTreeItem.getValue());
                        }
                        else if (childTreeItem.getValue() instanceof Control) {
                            return !controls.contains(childTreeItem.getValue());
                        }

                    }

                    return true;
                });

            });

        }
    }

    private void traverseControls(TreeItem<Object> treeItem, Spatial sceneItem) {

        ObservableList<TreeItem<Object>> treeChildren = treeItem.getChildren();
        List<Control> controls = new ArrayList<>();

        for (int i = 0 ; i < sceneItem.getNumControls(); i++) {
            controls.add(sceneItem.getControl(i));
        }

        // check if it exists in the tree, and if not, add it.
        for (Control control : controls) {

            boolean existsTree = treeChildren.stream().anyMatch(treeChild -> {

                if (treeChild.getValue() != null && treeChild.getValue() instanceof Control) {
                    Control treeControl = (Control) treeChild.getValue();
                    // return controls.contains(treeControl);
                    return control == treeControl;
                }

                return false;
            });

            // if exists in the scene and it doesnt exist in the tree, add it.
            if (!existsTree) {
                ControlTreeItem controlTreeItem = new ControlTreeItem(control);
                treeItem.getChildren().add(controlTreeItem);
            }
        }

        // check if it exists in the tree and not the scene
        treeChildren.removeIf(treeChild -> {

            if (treeChild.getValue() != null && treeChild.getValue() instanceof Control) {
                Control control = (Control) treeChild.getValue();
                return !controls.contains(control);
            }

            return false;
        });

    }

    private void traverseLights(TreeItem<Object> treeItem, Spatial sceneItem) {

        ObservableList<TreeItem<Object>> treeChildren = treeItem.getChildren();
        List<Light> lights = new ArrayList<>();

        for (int i = 0 ; i < sceneItem.getLocalLightList().size(); i++) {
            lights.add(sceneItem.getLocalLightList().get(i));
        }

        // check if it exists in the tree, and if not, add it.
        for (Light light : lights) {

            boolean existsTree = treeChildren.stream().anyMatch(treeChild -> {

                if (treeChild.getValue() != null && treeChild.getValue() instanceof Light) {
                    Light treeLight = (Light) treeChild.getValue();
                    // boolean contains = lights.contains(treeLight);
                    boolean contains = treeLight == light;
                    return contains;
                }

                return false;
            });

            // if exists in the scene and it doesnt exist in the tree, add it.
            if (!existsTree) {
                LightTreeItem lightTreeItem = new LightTreeItem(light, null);
                treeItem.getChildren().add(lightTreeItem);
            }
        }

        // check if it exists in the tree and not the scene

        treeChildren.removeIf(treeChild -> {

            if (treeChild.getValue() != null && treeChild.getValue() instanceof Light) {
                Light light = (Light) treeChild.getValue();
                boolean contains =  lights.contains(light);
                return !contains;
            }

            return false;
        });

    }

}
