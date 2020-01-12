package com.jayfella.sdk.sdk.tree.project;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class FolderTreeItem extends ProjectTreeItem {

    public FolderTreeItem(File value) {
        super(value, new ImageView(new Image(FolderTreeItem.class.getResourceAsStream("/Icons/File/folder-solid.png"))));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a folder."));
    }
}
