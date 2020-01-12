package com.jayfella.sdk.controller;

import com.jayfella.sdk.core.background.BackgroundTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BackgroundTaskListCell implements Initializable {

    @FXML private TitledPane titledPane;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setTask(BackgroundTask task) {
        titledPane.setText(task.getName());
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.statusProperty());
    }

}
