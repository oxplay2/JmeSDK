package com.jayfella.sdk.sdk.tree.scene;

import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CollectionTreeItem extends SceneTreeItem {

    public CollectionTreeItem(String value) {
        super(value, new ImageView(new Image("/Icons/list-solid.png")));
    }

    @Override
    public ContextMenu getMenu() {
        return null;
    }
}
