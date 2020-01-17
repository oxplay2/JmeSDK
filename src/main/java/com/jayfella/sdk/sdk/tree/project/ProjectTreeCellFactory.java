package com.jayfella.sdk.sdk.tree.project;

import com.jayfella.sdk.core.DnDFormat;
import com.jayfella.sdk.dialog.Alerts;
import com.jayfella.sdk.dialog.ImportModelDialog;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.model.ModelImporter;
import com.jayfella.sdk.project.Project;
import com.jayfella.sdk.service.ProjectExplorerService;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.io.File;
import java.util.List;

public class ProjectTreeCellFactory implements Callback<TreeView<Object>, TreeCell<Object>> {

    private TreeItem<Object> draggedItem;

    private final Project project;

    public ProjectTreeCellFactory(Project project) {
        this.project = project;
    }

    @Override
    public TreeCell<Object> call(TreeView<Object> treeView) {

        TreeCell<Object> cell = new ProjectTreeCell();

        cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
        cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
        cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());

        // If the cell accepts the drag as viable, highlight it.
        cell.setOnDragEntered(event -> cell.setOpacity(0.3));
        cell.setOnDragExited(event -> cell.setOpacity(1));

        return cell;
    }

    private void dragDetected(MouseEvent event, TreeCell<Object> treeCell, TreeView<Object> treeView) {

        draggedItem = treeCell.getTreeItem();

        if (draggedItem.getParent() == null) return; // root can't be dragged
        if (draggedItem instanceof FolderTreeItem) return; // folders can't be dragged


        DataFormat dataFormat = null;
        String data = "null";

        // this is a resource, so it will have a resource path we can send.
        if (draggedItem instanceof ResourceModelTreeItem) {

            dataFormat = DnDFormat.PROJECT_RESOURCE;

            // the data is a file of the resource, so we need to reduce the path.
            File file  = (File) draggedItem.getValue();
            String assetPath = file.getPath().replace(project.getResourcesRoot().toString() + "/", "");
            data = assetPath;

        }

        // this is a class that extends Control, so we'll send a string reference to the class.
        else if (draggedItem instanceof  ControlTreeItem) {

            dataFormat = DnDFormat.PROJECT_CLASS;

            Class<?> classControl = (Class<?>) draggedItem.getValue();
            data = classControl.getName();
        }

        // this is a class that extends Spatial, so we'll send a string reference to the class.
        else if (draggedItem instanceof SpatialTreeItem) {
            dataFormat = DnDFormat.PROJECT_CLASS;

            Class<?> classControl = (Class<?>) draggedItem.getValue();
            data = classControl.getName();
        }

        if (dataFormat == null) {
            return;
        }

        Dragboard db = treeCell.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();

        content.put(dataFormat, data);

        db.setContent(content);
        db.setDragView(treeCell.snapshot(null, null));
        event.consume();

    }

    private void dragOver(DragEvent event, TreeCell<Object> treeCell, TreeView<Object> treeView) {

        // dragging files into the project:
        // - must be the resources directory
        // - must be a GLTF or GLB file
        // - file must not already exist.

        if (event.getDragboard().hasFiles()) {

            if (treeCell.getTreeItem() instanceof FolderTreeItem) {

                File file = (File) treeCell.getTreeItem().getValue();

                if (file != null && file.isDirectory() && file.exists()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    event.consume();
                }

            }

        }

    }

    private void drop(DragEvent event, TreeCell<Object> treeCell, TreeView<Object> treeView) {

        if (event.getDragboard().hasFiles()) {

            final String[] supportedExtensions = {
                    "gltf", "glb"
            };

            // remove any files with extensions we don't support
            List<File> files =  event.getDragboard().getFiles();
            files.removeIf(file -> {

                boolean remove = true;

                for (String ext : supportedExtensions) {
                     if (file.getName().toLowerCase().endsWith(ext)) {
                         remove = false;
                     }
                }

                return remove;
            });

            if (files.isEmpty()) {

                Alert alert = Alerts.error(
                        "No Models Found",
                        "No Models Found",
                        "Could not find any compatible models.");

                alert.show();
                return;

            }

            if (files.size() > 1) {

                Alert alert = Alerts.error(
                        "Too Many Models",
                        "Too Many Models",
                        "Please only drag one model in at a time.");

                alert.show();
                return;

            }

            event.setDropCompleted(true);
            event.consume();

            ImportModelDialog dialog = new ImportModelDialog();
            boolean proceed = dialog.show();

            if (proceed) {

                AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();

                File modelFile = files.get(0);
                File dest = (File) treeCell.getTreeItem().getValue();

                String srcRoot = modelFile.getParent(); // the directory that the source model resides.
                String targetRoot = Project.getOpenProject().getResourcesRoot().toFile().getAbsolutePath();

                String targetAssetPath = dest.getAbsolutePath()
                        .replace(Project.getOpenProject().getResourcesRoot() + "/", "")
                        + "/" + dialog.getDirectoryName();

                String modelPath = modelFile.getAbsolutePath(); // the file we are importing.

                assetManager.registerLocator(modelFile.getParentFile().getAbsolutePath(), FileLocator.class);

                ModelImporter modelImporter = new ModelImporter();
                modelImporter.begin(srcRoot, targetRoot, targetAssetPath, modelPath);

                assetManager.unregisterLocator(srcRoot, FileLocator.class);

                // FolderTreeItem folderTreeItem = (FolderTreeItem) treeCell.getTreeItem();

                FolderTreeItem folderTreeItem = new FolderTreeItem(new File(targetAssetPath));
                treeCell.getTreeItem().getChildren().add(folderTreeItem);

                File target = new File(dest.getAbsolutePath() + "/" + dialog.getDirectoryName());

                // add the new items to the tree
                ServiceManager.getService(ProjectExplorerService.class).traverseProjectFileSystem(folderTreeItem, target);

            }

        }

    }

    private void clearDropLocation() {

    }

}
