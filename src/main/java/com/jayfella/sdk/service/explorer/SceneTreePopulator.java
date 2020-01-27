package com.jayfella.sdk.service.explorer;

import com.jayfella.sdk.sdk.tree.scene.*;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import javafx.scene.control.TreeItem;

import java.util.List;

public class SceneTreePopulator {

    private Node scene;

    public SceneTreePopulator() {

    }

    public void setScene(Node scene) {
        this.scene = scene;
    }

    public NodeTreeItem traverse() {
        return traverse(true, true, true);
    }

    public NodeTreeItem traverse(boolean geometries, boolean controls, boolean lights) {

        if (scene == null) {
            return null;
        }

        NodeTreeItem root = new NodeTreeItem(scene);

        if (lights) {
            findLights(root, scene);
        }

        if (controls) {
            findControls(root, scene);
        }

        traverseScene(root, scene, geometries, controls, lights);

        return root;
    }

    public void traverseScene(TreeItem<Object> treeNode, Spatial spatial, boolean geometries, boolean controls, boolean lights) {

        if (spatial instanceof Node) {

            List<Spatial> children = ((Node) spatial).getChildren();
            for (Spatial child : children) {

                if (child instanceof Node) {
                    NodeTreeItem childTreeItem = new NodeTreeItem((Node) child);


                    if (lights) {
                        findLights(childTreeItem, child);
                    }

                    if (controls) {
                        findControls(childTreeItem, child);
                    }

                    treeNode.getChildren().add(childTreeItem);
                    traverseScene(childTreeItem, child, geometries, controls, lights);
                }
                else {
                    traverseScene(treeNode, child, geometries, controls, lights);
                }
            }
        }
        else if (geometries && spatial instanceof Geometry) {
            Geometry geometry = (Geometry) spatial;
            GeometryTreeItem geomTreeItem = new GeometryTreeItem(geometry);
            treeNode.getChildren().add(geomTreeItem);

            if (lights) {
                findLights(geomTreeItem, geometry);
            }

            if (controls) {
                findControls(geomTreeItem, geometry);
            }
        }
    }

    private void findLights(TreeItem<Object> treeNode, Spatial spatial) {

        if (spatial.getLocalLightList().size() > 0) {

            for (Light light : spatial.getLocalLightList()) {
                LightTreeItem lightTreeItem = new LightTreeItem(light, null);
                treeNode.getChildren().add(lightTreeItem);
            }
        }

    }

    private void findControls(TreeItem<Object> treeNode, Spatial spatial) {

        if (spatial.getNumControls() > 0) {

            for (int i = 0; i < spatial.getNumControls(); i++) {
                ControlTreeItem controlTreeItem = new ControlTreeItem(spatial.getControl(i));
                treeNode.getChildren().add(controlTreeItem);
            }

        }

    }

}
