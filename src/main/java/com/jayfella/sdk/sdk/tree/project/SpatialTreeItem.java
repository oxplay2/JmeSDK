package com.jayfella.sdk.sdk.tree.project;

import com.jme3.scene.Spatial;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class SpatialTreeItem extends ProjectTreeItem {

    public SpatialTreeItem(Class<? extends Spatial> value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.CODE_FORK));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a spatial class."));
    }
}
