package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.scene.Geometry;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class GeometryTreeItem extends SceneTreeItem {

    public GeometryTreeItem(Geometry value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.CUBE));
    }

    @Override
    public ContextMenu getMenu() {

        Geometry item = (Geometry) getValue();
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE));

        deleteItem.setOnAction(event -> {
            ThreadRunner.runInJmeThread(item::removeFromParent);
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }

}
