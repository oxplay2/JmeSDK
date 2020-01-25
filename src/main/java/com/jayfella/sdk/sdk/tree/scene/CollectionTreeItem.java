package com.jayfella.sdk.sdk.tree.scene;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

public class CollectionTreeItem extends SceneTreeItem {

    public CollectionTreeItem(String value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.LIST));
    }

    @Override
    public ContextMenu getMenu() {
        return null;
    }
}
