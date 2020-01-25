package com.jayfella.sdk.controller;

import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.sdk.editor.SpatialToolState;
import com.jayfella.sdk.service.SceneEditorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public class SceneEditorGUI implements Initializable {

    @FXML private ChoiceBox<Float> translationChoiceBox;
    @FXML private ChoiceBox<Float> rotateStepChoiceBox;
    @FXML private ChoiceBox<Float> scaleStepChoiceBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        translationChoiceBox.getItems().addAll(0.1f, 0.25f, 0.5f, 1.0f, 10.0f, 100.0f);
        rotateStepChoiceBox.getItems().addAll(1.0f, 5.0f, 10.0f, 15.0f, 30.0f, 45.0f, 60.0f, 90.0f, 120.0f);
        scaleStepChoiceBox.getItems().addAll(0.1f, 0.25f, 0.5f, 1.0f, 10.0f, 100.0f);

        translationChoiceBox.getSelectionModel().select(0);
        rotateStepChoiceBox.getSelectionModel().select(0);
        scaleStepChoiceBox.getSelectionModel().select(0);
    }

    @FXML
    private void onAmbientLightToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        ServiceManager.getService(SceneEditorService.class)
                .setAmbientLightAttached(toggleButton.isSelected());

    }

    @FXML
    private void onDirectionalLightToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        ServiceManager.getService(SceneEditorService.class)
                .setDirectionalLightAttached(toggleButton.isSelected());

    }

    @FXML
    private void onLightProbeToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        ServiceManager.getService(SceneEditorService.class)
                .setLightProbeAttached(toggleButton.isSelected());

    }

    private void setActiveTransformTool(SpatialToolState.Tool tool) {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        ThreadRunner.runInJmeThread(() ->
                engineService.getStateManager().getState(SpatialToolState.class).setTool(tool));
    }

    @FXML
    private void onMoveToolToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        if (toggleButton.isSelected()) {
            setActiveTransformTool(SpatialToolState.Tool.Move);
        }

    }

    @FXML
    private void onRotateToolToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        if (toggleButton.isSelected()) {
            setActiveTransformTool(SpatialToolState.Tool.Rotate);
        }

    }

    @FXML
    private void onScaleToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        if (toggleButton.isSelected()) {
            setActiveTransformTool(SpatialToolState.Tool.Scale);
        }

    }

    @FXML
    private void onMode2dToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        if (toggleButton.isSelected()) {
            ThreadRunner.runInJmeThread(() -> ServiceManager.getService(JmeEngineService.class).setView2d());
            ServiceManager.getService(SceneEditorService.class).set2d();
        }

    }

    @FXML
    private void onMode3dToggle(ActionEvent event) {

        ToggleButton toggleButton = (ToggleButton) event.getSource();

        if (toggleButton.isSelected()) {
            ThreadRunner.runInJmeThread(() -> ServiceManager.getService(JmeEngineService.class).setView3d());
            ServiceManager.getService(SceneEditorService.class).set3d();
        }

    }

}
