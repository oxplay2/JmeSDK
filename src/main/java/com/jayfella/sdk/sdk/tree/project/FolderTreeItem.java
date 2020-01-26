package com.jayfella.sdk.sdk.tree.project;

import com.jayfella.sdk.dialog.Alerts;
import com.jayfella.sdk.dialog.CreateFolderDialog;
import com.jayfella.sdk.dialog.NewSceneDialog;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Represents a real folder that exists in the project.
 */
public class FolderTreeItem extends ProjectTreeItem {

    public FolderTreeItem(File value) {
        super(value, new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
    }

    @Override
    public ContextMenu getMenu() {
        // return new ContextMenu(new MenuItem("its a folder."));

        ContextMenu contextMenu = new ContextMenu();

        MenuItem newFolder = new MenuItem("Create Folder");
        newFolder.setOnAction(event -> {

            CreateFolderDialog createFolderDialog = new CreateFolderDialog();
            boolean createFolder = createFolderDialog.showAndWait();

            if (createFolder) {

                File currentDir = (File) getValue();

                File folder = new File(currentDir.getAbsolutePath(), createFolderDialog.getFolderName());
                boolean mkdirs = folder.mkdirs();

                if (mkdirs) {
                    FolderTreeItem folderTreeItem = new FolderTreeItem(folder);
                    getChildren().add(folderTreeItem);
                }
                else {

                    Alerts.error(
                            "Create Directory",
                            "Unable to create Directory",
                            "Could not create the directory.")
                            .show();

                }

            }

        });
        contextMenu.getItems().add(newFolder);

        // contextMenu.getItems().add(new SeparatorMenuItem());

        MenuItem newScene = new MenuItem("Create Scene");
        newScene.setOnAction(event -> {

            NewSceneDialog newSceneDialog = new NewSceneDialog();
            boolean create = newSceneDialog.showAndWait();

            if (create) {

                File currentDir = (File) getValue();
                File newSceneFile = new File(currentDir.getAbsolutePath(), newSceneDialog.getSceneName() + ".j3o");

                Node node = new Node("New Scene");

                try {
                    BinaryExporter.getInstance().save(node, newSceneFile);

                    ResourceModelTreeItem resourceModelTreeItem = new ResourceModelTreeItem(newSceneFile);
                    getChildren().add(resourceModelTreeItem);

                } catch (IOException e) {
                    // e.printStackTrace();

                    Alerts.error(
                            "Create New Scene",
                            "Unable to create scene.",
                            e.getMessage())
                            .show();
                }

            }

        });
        contextMenu.getItems().add(newScene);

        contextMenu.getItems().add(new SeparatorMenuItem());

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteItem.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you wish to permanently delete this folder and all of its content?",
                    ButtonType.YES, ButtonType.NO);

            alert.setTitle("Delete Folder");
            alert.setHeaderText("Confirm Delete Model");

            Optional<ButtonType> button = alert.showAndWait();

            if (button.isPresent() && button.get() == ButtonType.YES) {

                File folder = (File) getValue();

                // boolean deleted = modelFile.delete();
                try {
                    FileUtils.deleteDirectory(folder);
                    getParent().getChildren().remove(this);
                } catch (IOException e) {

                    e.printStackTrace();

                    Alerts.error("Delete Folder",
                            "Unable to Delete",
                            "An error occurred attempting to delete the requested folder: " + e.getMessage())
                            .show();
                }

            }

        });
        contextMenu.getItems().add(deleteItem);

        return contextMenu;
    }
}
