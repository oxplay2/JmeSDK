package com.jayfella.sdk.service;

import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.ext.registrar.control.ControlRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.project.Project;
import com.jayfella.sdk.sdk.tree.project.*;
import com.jayfella.sdk.service.registration.ControlRegistration;
import com.jayfella.sdk.service.registration.SpatialRegistration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProjectExplorerService implements Service, Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProjectExplorerService.class);

    @FXML private VBox root;
    @FXML private TreeView<Object> projectTree;

    public ProjectExplorerService() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/ProjectExplorer.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Parent getJfxControl() {
        return root;
    }

    @Override
    public void stopService() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        AnchorPane.setTopAnchor(root, 0d);
        AnchorPane.setBottomAnchor(root, 0d);
        AnchorPane.setLeftAnchor(root, 0d);
        AnchorPane.setRightAnchor(root, 0d);

        projectTree.setCellFactory(new ProjectTreeCellFactory(Project.getOpenProject()));

        projectTree.setOnMouseClicked(mouseEvent -> {

            if (mouseEvent.getClickCount() == 2) {

                TreeItem<Object> treeItem = projectTree.getSelectionModel().getSelectedItem();

                if (treeItem != null) {

                    if (treeItem instanceof FolderTreeItem) {
                        // do nothing.
                    }
                    else if (treeItem instanceof ResourceModelTreeItem) {

                        File file = (File) treeItem.getValue();

                        if (file.getName().toLowerCase().endsWith(".j3o")) {
                            ServiceManager.getService(SceneExplorerService.class).openFile(file);
                        }

                    }

                }
            }

        });

    }

    public void populateProjectFilesTreeView() {

        File parent = new File(Project.getOpenProject().getProjectPath());

        FolderTreeItem root = new FolderTreeItem(parent);
        projectTree.setRoot(root);

        // we need to read from the JAR.

        // Spatials
        FakeFolderTreeItem spatialsFolderTreeItem = new FakeFolderTreeItem("Spatials");
        root.getChildren().add(spatialsFolderTreeItem);
        findSpatials(spatialsFolderTreeItem);

        // Controls
        FakeFolderTreeItem controlsFolderTreeItem = new FakeFolderTreeItem("Controls");
        root.getChildren().add(controlsFolderTreeItem);
        findControls(controlsFolderTreeItem);

        // we can read directly from the file structure
        // we do this for resources so we can save them.

        // resources
        FolderTreeItem resourcesFolderTreeItem = new FolderTreeItem(Project.getOpenProject().getResourcesRoot().toFile());
        root.getChildren().add(resourcesFolderTreeItem);
        traverseProjectFileSystem(resourcesFolderTreeItem, Project.getOpenProject().getResourcesRoot().toFile());
    }

    private void findControls(ProjectTreeItem treeItem) {

        ControlRegistration controlRegistration = ServiceManager.getService(RegistrationService.class).getControlRegistration();

        for (ControlRegistrar registrar : controlRegistration.getRegisteredControls()) {

            ControlTreeItem controlTreeItem = new ControlTreeItem(registrar.getRegisteredClass());
            treeItem.getChildren().add(controlTreeItem);

            log.info("Adding Registered Control: " + registrar.getRegisteredClass().getName());
        }
    }

    private void findSpatials(ProjectTreeItem treeItem) {

        SpatialRegistration spatialRegistration = ServiceManager.getService(RegistrationService.class).getSpatialRegistration();

        for (SpatialRegistrar registrar : spatialRegistration.getRegisteredSpatials()) {

            SpatialTreeItem spatialTreeItem = new SpatialTreeItem(registrar.getRegisteredClass());
            treeItem.getChildren().add(spatialTreeItem);

            log.info("Adding Registered Spatial: " + registrar.getRegisteredClass().getName());
        }

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

            if (parent.getName().toLowerCase().endsWith(".j3o")) {
                ResourceModelTreeItem resourceModelTreeItem = new ResourceModelTreeItem(parent);
                treeItem.getChildren().add(resourceModelTreeItem);
            }
            else {
                UnknownTreeItem unknownTreeItem = new UnknownTreeItem(parent);
                treeItem.getChildren().add(unknownTreeItem);
            }


        }

    }

}
