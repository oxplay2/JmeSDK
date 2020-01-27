package com.jayfella.sdk.dialog;

import com.jayfella.sdk.ext.core.FloatTextFormatter;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.sdk.tree.scene.NodeTreeItem;
import com.jayfella.sdk.service.SceneExplorerService;
import com.jayfella.sdk.service.explorer.SceneTreePopulator;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewLightProbeDialog implements Initializable {

    private Stage stage;

    @FXML private TreeView<Object> treeView;
    @FXML private ChoiceBox<LightProbe.AreaType> areaTypeChoiceBox;
    @FXML private TextField radiusTextField;

    private Node selectedNode;

    public Node showAndWait() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/Dialog/NewLightProbe.fxml"));
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

        return selectedNode;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // populate the tree
        Node scene = ServiceManager.getService(SceneExplorerService.class)
                .getAttachedScene();

        SceneTreePopulator populator = new SceneTreePopulator();
        populator.setScene(scene);

        NodeTreeItem root = populator.traverse(false, false, false);
        treeView.setRoot(root);

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedNode = (Node) newValue.getValue();
        });

        // area type choiceBox
        areaTypeChoiceBox.getItems().addAll(LightProbe.AreaType.values());
        areaTypeChoiceBox.getSelectionModel().select(LightProbe.AreaType.Spherical);

        radiusTextField.setText("100.0");
        radiusTextField.setTextFormatter(new FloatTextFormatter());
    }

    @FXML
    private void createButtonPressed(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        selectedNode = null;
        stage.close();
    }

    public LightProbe.AreaType getSelectedAreaType() {
        return areaTypeChoiceBox.getSelectionModel().getSelectedItem();
    }

    public float getSelectedRadius() {
        return Float.parseFloat(radiusTextField.getText());
    }

}
