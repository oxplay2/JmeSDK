package com.jayfella.sdk.sdk.tree.project;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

/**
 * Represents a folder in the Project Tree that does not exist. Used primarily for organizing items.
 */
public class FakeFolderTreeItem extends ProjectTreeItem {

    public FakeFolderTreeItem(String value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
    }

    @Override
    public ContextMenu getMenu() {
        return null; // no menu for a fake folder.
    }
}
