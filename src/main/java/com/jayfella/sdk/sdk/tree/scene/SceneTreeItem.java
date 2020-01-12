package com.jayfella.sdk.sdk.tree.scene;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public abstract class SceneTreeItem extends TreeItem<Object> {

    public SceneTreeItem(Object value, Node graphic) {
        super(value, graphic);
    }

    public abstract ContextMenu getMenu();

}
