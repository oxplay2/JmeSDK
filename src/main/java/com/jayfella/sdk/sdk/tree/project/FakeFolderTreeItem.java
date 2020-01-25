package com.jayfella.sdk.sdk.tree.project;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;

public class FakeFolderTreeItem extends ProjectTreeItem {

    public FakeFolderTreeItem(String value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
    }

    @Override
    public ContextMenu getMenu() {
        return null; // no menu for a fake folder.
    }
}
