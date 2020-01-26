package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class LightTreeItem extends SceneTreeItem {

    public LightTreeItem(Object value, Node graphic) {
        super(value, graphic);
    }

    @Override
    public ContextMenu getMenu() {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteItem.setOnAction(event -> {

            Light light = (Light) getValue();
            Spatial lightParent = (Spatial) getParent().getValue();

            ThreadRunner.runInJmeThread(() -> lightParent.removeLight(light));

            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }
}
