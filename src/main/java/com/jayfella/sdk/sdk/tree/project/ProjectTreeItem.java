package com.jayfella.sdk.sdk.tree.project;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public abstract class ProjectTreeItem extends TreeItem<Object> {

    public ProjectTreeItem(Object value, Node graphic) {
        super(value, graphic);
    }

    public abstract ContextMenu getMenu();

}
