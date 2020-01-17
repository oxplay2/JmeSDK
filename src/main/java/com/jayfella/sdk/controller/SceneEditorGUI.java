package com.jayfella.sdk.controller;

import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.sdk.editor.SpatialToolState;
import com.jayfella.sdk.service.SceneEditorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public class SceneEditorGUI implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

}
