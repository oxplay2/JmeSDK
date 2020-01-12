package com.jayfella.sdk.sdk.tree.project;

import com.jme3.scene.control.Control;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControlTreeItem extends ProjectTreeItem {

    public ControlTreeItem(Class<? extends Control> value) {
        super(value, new ImageView(new Image(ControlTreeItem.class.getResourceAsStream("/Icons/File/file-regular.png"))));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a control."));
    }
}
