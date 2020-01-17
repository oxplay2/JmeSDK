package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControlTreeItem extends SceneTreeItem {

    public ControlTreeItem(Control value) {
        super(value, null);
    }

    @Override
    public ContextMenu getMenu() {

        Control item = (Control) getValue();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new ImageView(new Image("/Icons/times-circle-regular.png")));

        deleteItem.setOnAction(event -> {

            Spatial controlParent = (Spatial) getParent().getValue();

            ThreadRunner.runInJmeThread(() -> controlParent.removeControl(item));
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }
}
