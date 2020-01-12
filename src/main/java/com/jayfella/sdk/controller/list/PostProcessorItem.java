package com.jayfella.sdk.controller.list;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class PostProcessorItem implements Initializable {

    @FXML private CheckBox checkBox;
    @FXML private Label label;
    @FXML private Button configButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public Label getLabel() {
        return label;
    }

    public Button getConfigButton() {
        return configButton;
    }

}
