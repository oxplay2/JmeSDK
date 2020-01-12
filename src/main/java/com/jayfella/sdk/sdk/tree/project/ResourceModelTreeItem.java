package com.jayfella.sdk.sdk.tree.project;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ResourceModelTreeItem extends ProjectTreeItem {

    public ResourceModelTreeItem(File value) {
        super(value, new ImageView(new Image(ResourceModelTreeItem.class.getResourceAsStream("/Icons/File/file-regular.png"))));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a j3o."));
    }
}
