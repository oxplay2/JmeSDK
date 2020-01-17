package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NodeTreeItem extends SceneTreeItem {

    public NodeTreeItem(Node node) {
        super(node, new ImageView(new Image(NodeTreeItem.class.getResourceAsStream("/Icons/Jme/Scene/node.png"))));
    }

    @Override
    public ContextMenu getMenu() {

        Spatial item = (Spatial) getValue();
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new ImageView(new Image("/Icons/times-circle-regular.png")));

        deleteItem.setOnAction(event -> {
            ThreadRunner.runInJmeThread(item::removeFromParent);
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        // contextMenu.getItems().add(new SeparatorMenuItem());



        return contextMenu;
    }
}
