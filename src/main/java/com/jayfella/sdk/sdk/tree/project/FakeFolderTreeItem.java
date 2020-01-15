package com.jayfella.sdk.sdk.tree.project;

import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FakeFolderTreeItem extends ProjectTreeItem {

    public FakeFolderTreeItem(String value) {
        super(value, new ImageView(new Image(FolderTreeItem.class.getResourceAsStream("/Icons/File/folder-solid.png"))));
    }

    @Override
    public ContextMenu getMenu() {
        return null; // no menu for a fake folder.
    }
}
