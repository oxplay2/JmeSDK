package com.jayfella.sdk.sdk.tree.scene;

import com.jme3.light.Light;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import javafx.scene.control.TreeCell;

public class SceneTreeCell extends TreeCell<Object> {

    @Override
    public void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {

            String text = "No Name";

            if (item instanceof String) {
                text = item.toString();
            }

            if (item instanceof Spatial) {
                Spatial spatial = (Spatial) item;

                if (spatial.getName() != null) {
                    text = spatial.getName();
                }
            }

            else if (item instanceof Light) {

                Light light = (Light) item;

                if (light.getName() != null) {
                    text = light.getName();
                }
            }

            if (!(item instanceof String)) {
                text += " [" + item.getClass().getSimpleName() + "]";
            }

            // the objects have no name.
            if (item instanceof Control || item instanceof Mesh) {
                text = item.getClass().getSimpleName();
            }

            setText(text);

            setGraphic(getTreeItem().getGraphic());
            setContextMenu(((SceneTreeItem) getTreeItem()).getMenu());
        }
    }



}
