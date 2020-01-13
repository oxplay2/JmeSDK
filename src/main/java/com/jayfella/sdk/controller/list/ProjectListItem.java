package com.jayfella.sdk.controller.list;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ProjectListItem implements Initializable {

    @FXML private Label projectNameLabel;
    @FXML private Label projectPathLabel;
    @FXML private Button removeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public Label getProjectNameLabel() {
        return projectNameLabel;
    }

    public Label getProjectPathLabel() {
        return projectPathLabel;
    }

    public Button getRemoveButton() {
        return removeButton;
    }
}
