package com.jayfella.sdk.sdk.tree.project;

import com.jme3.scene.control.Control;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ControlTreeItem extends ProjectTreeItem {

    public ControlTreeItem(Class<? extends Control> value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.GAMEPAD));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a control."));
    }
}
