package com.jayfella.sdk.controller;

import com.jayfella.sdk.core.background.BackgroundTask;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.service.BackgroundTaskService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BackgroundTasksControl implements Initializable {

    @FXML private Label statusLabel;
    @FXML private ProgressBar totalProgressBar;

    private BackgroundTaskService backgroundTaskService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        backgroundTaskService = ServiceManager.getService(BackgroundTaskService.class);

        statusLabel.textProperty().bind(backgroundTaskService.statusProperty());
        totalProgressBar.progressProperty().bind(backgroundTaskService.progressTotalProperty());

        // @todo remove listener when this control is no longer visible.
        backgroundTaskService.getTasks().addListener((ListChangeListener<BackgroundTask>) change -> {
            totalProgressBar.setVisible(change.getList().size() > 0);
        });

        totalProgressBar.setOnMouseClicked(mouseEvent -> {

            if (!backgroundTaskService.getTasks().isEmpty()) {

                FXMLLoader primaryLoader = new FXMLLoader(getClass().getResource("/Interface/BackgroundTasksListControl.fxml"));

                Parent root = null;

                try {
                    root = primaryLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(totalProgressBar.getScene().getWindow());
                stage.setTitle("background Tasks");
                stage.setScene(new Scene(root));

                stage.setX(mouseEvent.getScreenX());
                stage.setY(mouseEvent.getScreenY());

                stage.show();
            }
        });

    }



}
