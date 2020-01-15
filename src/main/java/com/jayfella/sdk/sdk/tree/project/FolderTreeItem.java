package com.jayfella.sdk.sdk.tree.project;

import com.jayfella.sdk.dialog.Alerts;
import com.jayfella.sdk.dialog.CreateFolderDialog;
import com.jayfella.sdk.dialog.NewSceneDialog;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;

public class FolderTreeItem extends ProjectTreeItem {

    public FolderTreeItem(File value) {
        super(value, new ImageView(new Image(FolderTreeItem.class.getResourceAsStream("/Icons/File/folder-solid.png"))));
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

        return contextMenu;
    }
}
