package com.jayfella.sdk.service;

import com.jayfella.sdk.ext.core.Service;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.registrar.Selectable;
import com.jayfella.sdk.ext.registrar.spatial.SpatialRegistrar;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.ext.service.RegistrationService;
import com.jayfella.sdk.sdk.editor.SpatialToolState;
import com.jayfella.sdk.sdk.tree.scene.NodeTreeItem;
import com.jayfella.sdk.sdk.tree.scene.SceneTreeCellFactory;
import com.jayfella.sdk.service.explorer.SceneTreePopulator;
import com.jayfella.sdk.service.explorer.SceneTreeUpdater;
import com.jayfella.sdk.service.explorer.SpatialHighlighter;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SceneExplorerService implements Service, Initializable {

    private final BooleanProperty sceneOpen = new SimpleBooleanProperty(false);

    @FXML private VBox root;
    @FXML private TreeView<Object> sceneTree;
    @FXML private HBox toolbarHBox;
    @FXML private Label openFileLabel;

    private Node attachedScene;

    // used to save the currently opened file.
    private File openedFile = null;
    private ModelKey modelKey;

    // scene traversal: populating and updating
    // the populator populates the scene treeview. This is done the first time.
    // the updater updates an existing scene periodically(?).
    private final SceneTreePopulator treePopulator = new SceneTreePopulator();
    private final SceneTreeUpdater treeUpdater = new SceneTreeUpdater();

    private SpatialHighlighter spatialHighlighter = new SpatialHighlighter();

    public SceneExplorerService() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/SceneExplorer.fxml"));
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

    // keep track of the last selected item and selectable so we an un-select it when something else is selected.
    private Spatial lastSelectedSpatial;
    private Selectable lastSelectable;

    // displays a bounding box or a mesh shape of the selected item.
    private Geometry highlightGeom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        toolbarHBox.visibleProperty().bind(sceneOpen);
        toolbarHBox.managedProperty().bind(toolbarHBox.visibleProperty());

        this.sceneTree.setCellFactory(new SceneTreeCellFactory());
        this.sceneTree.setOnMouseClicked(mouseEvent -> {

            TreeItem<Object> treeItem = sceneTree.getSelectionModel().getSelectedItem();

            if (lastSelectable != null) {
                Application app = ServiceManager.getService(JmeEngineService.class);
                lastSelectable.itemUnselected(app, lastSelectedSpatial);

                lastSelectable = null;
                lastSelectedSpatial = null;
            }

            if (treeItem != null && treeItem.getValue() != null) {

                ServiceManager.getService(InspectorService2.class).setObject(treeItem.getValue());

                if (treeItem.getValue() instanceof Spatial) {

                    // activate the selected item.
                    Class<?> selectedItemClass = treeItem.getValue().getClass();

                    SpatialRegistrar registrar = ServiceManager.getService(RegistrationService.class).getSpatialRegistration()
                            .getRegistrations()
                            .stream()
                            .filter(spatialRegistrar -> spatialRegistrar.getRegisteredClass() == selectedItemClass)
                            .findFirst()
                            .orElse(null);

                    if (registrar instanceof Selectable) {

                        Spatial spatial = (Spatial) treeItem.getValue();

                        Selectable selectable = (Selectable) registrar;
                        lastSelectable = selectable;
                        lastSelectedSpatial = spatial;

                        Application app = ServiceManager.getService(JmeEngineService.class);
                        selectable.itemSelected(app, spatial);
                    }
                }

            }

        });

        // Any time an item is selected, highlight it with a transform tool
        // This is also invoked when an item is selected via the scene graph, as it selects the found spatial here.
        this.sceneTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {

                if (newValue.getValue() instanceof Spatial) {

                    Spatial spatial = (Spatial) newValue.getValue();

                    ThreadRunner.runInJmeThread(() -> {
                        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
                        SpatialToolState spatialToolState = engineService.getStateManager().getState(SpatialToolState.class);
                        spatialToolState.setSpatial(spatial);
                    });

                    if (spatial instanceof Node) {
                        spatialHighlighter.highlightBoundingShape(spatial);
                    }
                    else {
                        Geometry geometry = (Geometry) spatial;
                        spatialHighlighter.highlightMesh(geometry);
                    }
                }
                else {
                    spatialHighlighter.deleteHighlight();
                }
            }

        });

    }

    public void showHighlight() {
        spatialHighlighter.showHightlight();
    }

    public void removeHighlight() {
        spatialHighlighter.removeHighlight();
    }

    @FXML
    private void onSaveButtonClicked(ActionEvent event) {

        if (openedFile != null) {

            try {
                BinaryExporter.getInstance().save(attachedScene, openedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void onCloseButtonClicked(ActionEvent event) {

        if (attachedScene != null && attachedScene.getQuantity() > 0) {

            Alert warning = new Alert(
                    Alert.AlertType.WARNING,
                    "Any unsaved changes will be lost. Would you like to save this scene before you close it?",
                    ButtonType.YES, ButtonType.NO);

            warning.setTitle("Unsaved Changes");
            warning.setHeaderText("Changes Not Saved.");

            Optional<ButtonType> button = warning.showAndWait();
            if (button.isPresent() && button.get() == ButtonType.YES) {
                onSaveButtonClicked(event);
            }
        }

        sceneTree.setRoot(null);

        openFileLabel.setText("");

        if (attachedScene != null) {
            attachedScene.detachAllChildren();
        }

        openedFile = null;

        if (modelKey != null) {
            ServiceManager.getService(JmeEngineService.class).getAssetManager().deleteFromCache(modelKey);
            modelKey = null;
        }

        ServiceManager.getService(SceneEditorService.class).clearScene();
        ServiceManager.getService(InspectorService2.class).setObject(null);

        sceneOpen.setValue(false);

    }

    public void openFile(File file) {

        sceneOpen.setValue(true);

        openedFile = file;
        openFileLabel.setText(file.getName());

        AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();
        assetManager.registerLocator(file.getParent(), FileLocator.class);

        modelKey = new ModelKey(file.getName());
        Node model = (Node) ServiceManager.getService(JmeEngineService.class).getAssetManager().loadModel(modelKey);

        assetManager.unregisterLocator(file.getParent(), FileLocator.class);

        populateSceneTree(model);
        ServiceManager.getService(SceneEditorService.class).attachScene(model);
        // ServiceManager.getService(RegistrationService.class).getFilterRegistration().refreshFilters();
        ServiceManager.getService(JmeEngineService.class).getFilterManager().refreshFilters();
    }

    public void populateSceneTree(Node parent) {
        attachedScene = parent;

        treePopulator.setScene(parent);
        NodeTreeItem root = treePopulator.traverse();
        root.setExpanded(true);
        sceneTree.setRoot(root);

    }

    /**
     * Refreshes the entire scene tree by detecting any changes.
     */
    public void refresh() {
        refresh(sceneTree.getRoot());
    }

    /**
     * Refreshes a branch of the given scene tree by detecting any changes.
     * @param treeItem the treeItem to refresh.
     */
    public void refresh(TreeItem<Object> treeItem) {
        treeUpdater.refresh(treeItem, attachedScene);
    }

    public void setSelectedSpatial(Spatial spatial) {

        ThreadRunner.runInJfxThread(() -> {
            traverseTreeForSpatial(sceneTree.getRoot(), spatial);
            if (foundItem != null) {
                sceneTree.getSelectionModel().select(foundItem);
                sceneTree.scrollTo(sceneTree.getSelectionModel().getSelectedIndex());
            }
        });

    }

    private TreeItem<Object> foundItem = null;

    /**
     * Recursively finds the treeItem associated with the given spatial.
     * @param treeItem
     * @param spatial
     */
    private void traverseTreeForSpatial(TreeItem<Object> treeItem, Spatial spatial) {

        if (treeItem.getValue() == spatial) {
            foundItem = treeItem;
        }
        else {
            for (TreeItem<Object> child : treeItem.getChildren()) {
                traverseTreeForSpatial(child, spatial);
            }
        }
    }



    @Override
    public void stopService() {

    }


}
