package com.jayfella.sdk.sdk.tree.project;

import com.jme3.scene.Spatial;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpatialTreeItem extends ProjectTreeItem {

    public SpatialTreeItem(Class<? extends Spatial> value) {
        super(value, new ImageView(new Image(SpatialTreeItem.class.getResourceAsStream("/Icons/File/file-regular.png"))));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a spatial class."));
    }
}
