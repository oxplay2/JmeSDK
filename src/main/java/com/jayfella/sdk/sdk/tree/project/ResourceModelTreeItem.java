package com.jayfella.sdk.sdk.tree.project;

import com.jayfella.sdk.dialog.Alerts;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.util.Optional;

/**
 * Represents a j3o Model tree item.
 */
public class ResourceModelTreeItem extends ProjectTreeItem {

    public ResourceModelTreeItem(File value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.FILE));
    }

    @Override
    public ContextMenu getMenu() {
        // return new ContextMenu(new MenuItem("its a j3o."));

        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteItem.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you wish to permanently delete this model?",
                    ButtonType.YES, ButtonType.NO);

            alert.setTitle("Delete Model");
            alert.setHeaderText("Confirm Delete Model");

            Optional<ButtonType> button = alert.showAndWait();

            if (button.isPresent() && button.get() == ButtonType.YES) {

                File modelFile = (File) getValue();

                boolean deleted = modelFile.delete();

                if (deleted) {
                    getParent().getChildren().remove(this);
                }

                else Alerts.error("Delete Model",
                        "Unable to Delete",
                        "An error occurred attempting to delete the requested file.")
                        .show();

            }

        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }
}
