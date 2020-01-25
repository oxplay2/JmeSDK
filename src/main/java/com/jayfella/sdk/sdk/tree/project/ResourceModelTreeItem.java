package com.jayfella.sdk.sdk.tree.project;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.File;

public class ResourceModelTreeItem extends ProjectTreeItem {

    public ResourceModelTreeItem(File value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.FILE));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a j3o."));
    }
}
