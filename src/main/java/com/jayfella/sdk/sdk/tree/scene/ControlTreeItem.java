package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ControlTreeItem extends SceneTreeItem {

    public ControlTreeItem(Control value) {
        super(value, null);
    }

    @Override
    public ContextMenu getMenu() {

        Control item = (Control) getValue();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE));

        deleteItem.setOnAction(event -> {

            Spatial controlParent = (Spatial) getParent().getValue();

            ThreadRunner.runInJmeThread(() -> controlParent.removeControl(item));
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }
}
