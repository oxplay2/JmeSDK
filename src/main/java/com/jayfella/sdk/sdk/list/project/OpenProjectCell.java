package com.jayfella.sdk.sdk.list.project;

import com.jayfella.sdk.config.RecentProjects;
import com.jayfella.sdk.controller.list.ProjectListItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OpenProjectCell extends ListCell<File> {

    @Override
    protected void updateItem(final File item, final boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {

            Parent root = null;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/List/ProjectListItem.fxml"));
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProjectListItem projectListItem = fxmlLoader.getController();
            projectListItem.getProjectNameLabel().setText(item.getName());
            projectListItem.getProjectPathLabel().setText(item.toPath().toAbsolutePath().toString());

            projectListItem.getRemoveButton().setOnAction(event -> {

                RecentProjects recentProjects = RecentProjects.load();

                List<String> projects = recentProjects.getRecentProjects();
                projects.remove(item.getAbsolutePath());

                recentProjects.setRecentProjects(projects);
                recentProjects.save();

                getListView().getItems().remove(item);
            });

            // prefWidthProperty().bind(getListView().prefWidthProperty().subtract(20));
            prefWidthProperty().bind(getListView().widthProperty().subtract(18));
            setMaxWidth(Control.USE_PREF_SIZE);

            setText(null);
            setGraphic(root);
        }

    }

}
