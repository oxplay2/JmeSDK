package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.scene.Geometry;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GeometryTreeItem extends SceneTreeItem {

    public GeometryTreeItem(Geometry value) {
        super(value, new ImageView(new Image(GeometryTreeItem.class.getResourceAsStream("/Icons/Jme/Scene/geometry.png"))));
    }

    @Override
    public ContextMenu getMenu() {

        Geometry item = (Geometry) getValue();
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new ImageView(new Image("/Icons/times-circle-regular.png")));

        deleteItem.setOnAction(event -> {
            ThreadRunner.runInJmeThread(item::removeFromParent);
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }

}
