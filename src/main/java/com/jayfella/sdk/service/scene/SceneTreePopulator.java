package com.jayfella.sdk.service.scene;

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

        if (scene == null) {
            return null;
        }

        NodeTreeItem root = new NodeTreeItem(scene);

        findLights(root, scene);
        findControls(root, scene);
        traverseScene(root, scene);

        return root;
    }

    public void traverseScene(TreeItem<Object> treeNode, Spatial spatial) {

        if (spatial instanceof Node) {

            List<Spatial> children = ((Node) spatial).getChildren();
            for (Spatial child : children) {

                if (child instanceof Node) {
                    NodeTreeItem childTreeItem = new NodeTreeItem((Node) child);

                    findLights(childTreeItem, child);
                    findControls(childTreeItem, child);

                    treeNode.getChildren().add(childTreeItem);
                    traverseScene(childTreeItem, child);
                }
                else {
                    traverseScene(treeNode, child);
                }
            }
        }
        else if (spatial instanceof Geometry) {
            Geometry geometry = (Geometry) spatial;
            GeometryTreeItem geomTreeItem = new GeometryTreeItem(geometry);
            treeNode.getChildren().add(geomTreeItem);

            findLights(geomTreeItem, geometry);
            findControls(geomTreeItem, geometry);
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
