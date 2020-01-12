package com.jayfella.sdk.dialog;

import com.jayfella.sdk.ext.JmeScene;
import com.jayfella.sdk.project.Project;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class NewSceneDialog implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private Project project;

    @FXML
    private void onKeyPressed(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ENTER)) {

            TextField textField = (TextField) event.getSource();
            String name = textField.getText();

            // create a new Scene object

            Path scenesDir = project.getResourcesScenesDirectory();
            Path newDir = Paths.get(scenesDir.toString(), name);

            boolean mkdir = newDir.toFile().mkdir();

            JmeScene jmeScene = new JmeScene();

        }

    }

}
