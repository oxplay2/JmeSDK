package com.jayfella.sdk.controller;

import com.jayfella.sdk.core.background.BackgroundTask;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.service.BackgroundTaskService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BackgroundTasksListControl implements Initializable {

    @FXML private ListView<BackgroundTask> tasksListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        BackgroundTaskService taskService = ServiceManager.getService(BackgroundTaskService.class);

        tasksListView.setItems(taskService.getTasks());

        tasksListView.setCellFactory(backgroundTaskListView -> new BackgroundTaskCell());
    }

    private static class BackgroundTaskCell extends ListCell<BackgroundTask> {

        private Node graphic;
        private BackgroundTaskListCell controller;

        public BackgroundTaskCell() {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFx/BackgroundTask/BackgroundTaskListCell.fxml"));

            try {
                graphic = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            controller = loader.getController();

        }

        @Override
        protected void updateItem(BackgroundTask task, boolean empty) {

            if(empty || task == null) {
                setText(null);
                setGraphic(null);
            }
            else {

                controller.setTask(task);

                setText(null);
                setGraphic(graphic);

            }

        }


    }

}
