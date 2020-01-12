package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.core.DnDFormat;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.core.ThreadRunner;
import com.jayfella.sdk.ext.registrar.control.ControlRegistrar;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.service.JmeEngineService;
import com.jayfella.sdk.service.ProjectInjectorService;
import com.jayfella.sdk.service.RegistrationService;
import com.jayfella.sdk.service.SceneExplorerService;
import com.jme3.export.Savable;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import javafx.css.PseudoClass;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.util.Callback;

public class SceneTreeCellFactory implements Callback<TreeView<Object>, TreeCell<Object>> {

    private TreeItem<Object> draggedItem;

    @Override
    public TreeCell<Object> call(TreeView<Object> treeView) {

        TreeCell<Object> cell = new SceneTreeCell();

        cell.setOnDragDetected(event -> dragDetected(event, cell, treeView));
        cell.setOnDragOver(event -> dragOver(event, cell, treeView));
        cell.setOnDragDropped(event -> drop(event, cell, treeView));
        cell.setOnDragDone(event -> clearDropLocation());

        // If the cell accepts the drag as viable, highlight it.
        cell.setOnDragEntered(event -> cell.setOpacity(0.3));
        cell.setOnDragExited(event -> cell.setOpacity(1));

        return cell;
    }

    private void dragDetected(MouseEvent event, TreeCell<Object> treeCell, TreeView<Object> treeView) {

        draggedItem = treeCell.getTreeItem();

        if (draggedItem == null) return; // ... It happens.
        if (draggedItem.getParent() == null) return; // root can't be dragged
        if (!(draggedItem.getValue() instanceof Spatial)) return; // move spatials only

        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();

        content.put(DnDFormat.SCENE_OBJECT, 0);
        db.setContent(content);
        db.setDragView(treeCell.snapshot(null, null));
        event.consume();

    }

    private void dragOver(DragEvent event, TreeCell<Object> treeCell, TreeView<Object> treeView) {

        if (treeCell == null || treeCell.getTreeItem() == null) {
            return;
        }

        // scene-to-scene dragging
        if (event.getDragboard().hasContent(DnDFormat.SCENE_OBJECT)) {

            // if it's not the same
            if (treeCell.getTreeItem() != draggedItem) {

                // if the place we're dragging to is a Node
                if (treeCell.getTreeItem().getValue() instanceof Node) {

                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();

                }

            }

        }

        // dragging a j3o into the scene
        else if (event.getDragboard().hasContent(DnDFormat.PROJECT_RESOURCE)) {

            // we can only add a child to a node.
            if (treeCell.getTreeItem().getValue() instanceof Node) {
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        }

        // dragging a class (spatial, control, etc)
        else if (event.getDragboard().hasContent(DnDFormat.PROJECT_CLASS)) {

            // we can add controls to any spatial, and spatials to node only.
            if (treeCell.getTreeItem().getValue() instanceof Spatial) {
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        }
    }

    private void drop(DragEvent event, TreeCell<Object> treeCell, TreeView<Object> treeView) {

        // scene-to-scene dragging
        if (event.getDragboard().hasContent(DnDFormat.SCENE_OBJECT)) {

            // if it's not the same
            if (treeCell.getTreeItem() != draggedItem) {

                // if the place we're dragging to is a Node (accepts children)
                if (treeCell.getTreeItem().getValue() instanceof Node) {

                    Node dropNode = (Node) treeCell.getTreeItem().getValue();
                    Spatial itemToDrop = (Spatial) draggedItem.getValue();
                    dropNode.attachChild(itemToDrop);

                    // remove the dragged item from it's parent
                    draggedItem.getParent().getChildren().remove(draggedItem);

                    // add the dragged item to the new parent
                    treeCell.getTreeItem().getChildren().add(draggedItem);

                    event.setDropCompleted(true);
                    event.consume();

                }

            }
        }

        // dragging a j3o into the scene
        else if (event.getDragboard().hasContent(DnDFormat.PROJECT_RESOURCE)) {

            // if the place we're dragging to is a Node (accepts children)
            if (treeCell.getTreeItem().getValue() instanceof Node) {

                Node dropNode = (Node) treeCell.getTreeItem().getValue();

                // Spatial itemToDrop = (Spatial) draggedItem.getValue();
                // File file = (File) draggedItem.getValue();
                String resourcePath = (String) event.getDragboard().getContent(DnDFormat.PROJECT_RESOURCE);

                Spatial resource = ServiceManager.getService(JmeEngineService.class)
                        .getAssetManager()
                        .loadModel(resourcePath);

                dropNode.attachChild(resource);

                // add the new item to the scene
                // we also need to recurse it to add any children....
                NodeTreeItem nodeTreeItem = new NodeTreeItem((Node) resource);
                treeCell.getTreeItem().getChildren().add(nodeTreeItem);

                // @todo : update the scene
                // ServiceManager.getService(SceneExplorerService.class).traverseScene(nodeTreeItem, resource);

            }

        }

        // dragging a class (spatial, control, etc)
        else if (event.getDragboard().hasContent(DnDFormat.PROJECT_CLASS)) {

            String classString = (String) event.getDragboard().getContent(DnDFormat.PROJECT_CLASS);

            final Class<?> draggedClass;

            try {
                draggedClass = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader().loadClass(classString);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            // Controls can be added to any spatial
            if (isAssignable(draggedClass, Control.class)) {

                ControlRegistrar registrar = ServiceManager.getService(RegistrationService.class)
                        .getControlRegistration()
                        .getRegisteredControls()
                        .stream()
                        .filter(controlRegistrar -> controlRegistrar.getRegisteredClass().equals(draggedClass))
                        .findFirst()
                        .orElse(null);

                if (registrar == null) {
                    return;
                }

                if (treeCell.getTreeItem().getValue() instanceof Spatial) {

                    Spatial spatial = (Spatial) treeCell.getTreeItem().getValue();

                    Control control = registrar.createInstance(ServiceManager.getService(JmeEngineService.class));
                    ThreadRunner.runInJmeThread(() -> {
                        spatial.addControl(control);

                        // run after the item has been attached.
                        ThreadRunner.runInJfxThread(() -> {
                            ServiceManager.getService(SceneExplorerService.class).refresh();
                        });
                    });

                }

            }

            // Spatials can only be added to nodes.
            else if (isAssignable(draggedClass, Spatial.class)) {

                SpatialRegistrar registrar = ServiceManager.getService(RegistrationService.class)
                        .getSpatialRegistration()
                        .getRegisteredSpatials()
                        .stream()
                        .filter(spatialRegistrar -> spatialRegistrar.getRegisteredClass().equals(draggedClass))
                        .findFirst()
                        .orElse(null);

                if (registrar == null) {
                    return;
                }

                if (treeCell.getTreeItem().getValue() instanceof Node) {

                    Node node = (Node) treeCell.getTreeItem().getValue();

                    Spatial spatial = registrar.createInstance(ServiceManager.getService(JmeEngineService.class));

                    ThreadRunner.runInJmeThread(() -> {
                        node.attachChild(spatial);

                        // run after the item has been attached.
                        ThreadRunner.runInJfxThread(() -> {
                            ServiceManager.getService(SceneExplorerService.class).refresh();
                        });
                    });

                }

            }


        }

    }

    private void clearDropLocation() {

    }



    private boolean isAssignable(Class<?> clazz, Class<?> typeClass) {

        while (clazz != null) {

            if (clazz.isAssignableFrom(typeClass)) {
                return true;
            }

            Class<?>[] interfaces = clazz.getInterfaces();

            for (Class<?> interfaceClass : interfaces) {

                // lots of things implement savable. We don't want to match that.
                if (interfaceClass == Savable.class) {
                    continue;
                }

                if (interfaceClass.isAssignableFrom(typeClass)) {
                    return true;
                }
            }

            clazz = clazz.getSuperclass();

            // everything is assignable from object.
            if (clazz == Object.class) {
                break;
            }
        }

        return false;
    }

}
