package com.jayfella.sdk.sdk.tree.project;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.File;

public class UnknownTreeItem extends ProjectTreeItem {

    public UnknownTreeItem(File value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.QUESTION_CIRCLE));
    }

    @Override
    public ContextMenu getMenu() {
        return new ContextMenu(new MenuItem("its a unknown item."));
    }
}
