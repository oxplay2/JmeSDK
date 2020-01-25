package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class NodeTreeItem extends SceneTreeItem {

    public NodeTreeItem(Node node) {
        super(node, new FontAwesomeIconView(FontAwesomeIcon.CODE_FORK));
    }

    @Override
    public ContextMenu getMenu() {

        Spatial item = (Spatial) getValue();
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE));

        deleteItem.setOnAction(event -> {
            ThreadRunner.runInJmeThread(item::removeFromParent);
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        // contextMenu.getItems().add(new SeparatorMenuItem());



        return contextMenu;
    }
}
