package com.jayfella.sdk.dialog;

import com.jayfella.sdk.project.Project;
import com.jayfella.sdk.sdk.tree.project.*;
import com.jme3.util.SkyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewSkyBoxDialog implements Initializable {

    private static final String[] imageExtensions = { ".jpg", ".gif", ".png", ".dds", ".hdr" };

    private Stage stage;

    @FXML private TreeView<Object> treeView;
    @FXML private ChoiceBox<SkyFactory.EnvMapType> choiceBox;
    @FXML private Button okButton;

    private File selectedImage;

    public NewSkyBoxDialog() {

    }

    public File showAndWait() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/Dialog/NewSkyBox.fxml"));
        fxmlLoader.setController(this);

        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);

        stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle("Create Folder");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.showAndWait();

        return selectedImage;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choiceBox.getItems().addAll(SkyFactory.EnvMapType.values());
        choiceBox.getSelectionModel().select(SkyFactory.EnvMapType.EquirectMap);

        treeView.setShowRoot(false);
        treeView.setCellFactory(p -> new ProjectTreeCell());
        populateTreeView();

        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null && newValue.getValue() instanceof File) {

                File file = (File) newValue.getValue();

                if (!file.isDirectory()) {
                    selectedImage = (File) newValue.getValue();
                    okButton.setDisable(false);
                }
                else {
                    selectedImage = null;
                    okButton.setDisable(true);
                }
            }
            else {
                okButton.setDisable(true);
            }
        });

    }

    private void populateTreeView() {

        FakeFolderTreeItem root = new FakeFolderTreeItem("Resources");
        treeView.setRoot(root);

        // resources
        FolderTreeItem resourcesFolderTreeItem = new FolderTreeItem(Project.getOpenProject().getResourcesRoot().toFile());
        resourcesFolderTreeItem.setExpanded(true);
        root.getChildren().add(resourcesFolderTreeItem);
        traverseProjectFileSystem(resourcesFolderTreeItem, Project.getOpenProject().getResourcesRoot().toFile());
    }

    public void traverseProjectFileSystem(ProjectTreeItem treeItem, File parent) {

        if (parent.isDirectory()) {

            File[] files = parent.listFiles();

            if (files != null) {
                for (File file : files) {

                    if (file.isDirectory()) {

                        FolderTreeItem folderTreeItem = new FolderTreeItem(file);
                        treeItem.getChildren().add(folderTreeItem);

                        traverseProjectFileSystem(folderTreeItem, file);
                    }

                    else traverseProjectFileSystem(treeItem, file);
                }
            }

        }
        else {

            for (String ext : imageExtensions) {

                if (parent.getName().toLowerCase().endsWith(ext)) {
                    UnknownTreeItem unknownTreeItem = new UnknownTreeItem(parent);
                    treeItem.getChildren().add(unknownTreeItem);



                    break;
                }

            }

        }

    }

    @FXML
    private void okButtonPressed(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        selectedImage = null;
        stage.close();
    }

}
