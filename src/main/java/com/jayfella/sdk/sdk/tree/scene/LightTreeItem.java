package com.jayfella.sdk.sdk.tree.scene;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class LightTreeItem extends SceneTreeItem {

    public LightTreeItem(Object value, Node graphic) {
        super(value, graphic);
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a light."));
    }
}
