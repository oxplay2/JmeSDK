package com.jayfella.sdk.controller;

import com.jayfella.sdk.config.RecentProjects;
import com.jayfella.sdk.dialog.CustomDirectoryChooser;
import com.jayfella.sdk.project.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class WelcomePage implements Initializable {

    @FXML private ListView<String> recentProjectsListView;
    @FXML private GridPane gridPane;

    private MainPage primaryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // File file = new File("recentprojects.json");
        RecentProjects recentProjects = RecentProjects.load();
        recentProjectsListView.getItems().addAll(recentProjects.getRecentProjects());

        FXMLLoader backgroundTasksLoader = new FXMLLoader(getClass().getResource("/Interface/BackgroundTasksControl.fxml"));

        Parent loaderControl = null;
        try {
            loaderControl = backgroundTasksLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        gridPane.add(loaderControl, 0, 5);

        recentProjectsListView.setOnMouseClicked(mouseEvent -> {

            if (mouseEvent.getClickCount() == 2) {

                String selectedItem = recentProjectsListView.getSelectionModel().getSelectedItem();

                if (selectedItem != null) {

                    Project project = new Project(selectedItem);
                    openProject(project);
                }

            }

        });

    }

    @FXML
    public void newProjectButtonClicked(ActionEvent event) {

        FXMLLoader newProjectLoader = new FXMLLoader(getClass().getResource("/NewProjectPage.fxml"));

        Parent root = null;

        try {
            root = newProjectLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NewProjectPage controller = newProjectLoader.getController();
        controller.setPrimaryController(primaryController);
        controller.setWelcomeStage((Stage) gridPane.getScene().getWindow());

        Stage newProjectStage = new Stage(StageStyle.DECORATED);
        newProjectStage.setTitle("Create New Project");
        newProjectStage.setScene(new Scene(root, 780, 490));
        newProjectStage.show();

    }

    @FXML
    private void openProjectButtonClicked(ActionEvent event) {

        CustomDirectoryChooser customDirectoryChooser = new CustomDirectoryChooser();
        Path path = customDirectoryChooser.show();

        if (path != null) {

            Project project = new Project(path.toString());
            openProject(project);
        }
    }

    public void setPrimaryController(MainPage primaryController) {
        this.primaryController = primaryController;
    }

    private void openProject(Project project) {

        Project.setOpenProject(project);

        primaryController.start();
        primaryController.getMainStage().show();

        // build the project after we open it.
        primaryController.compileProject();

        Stage stage = (Stage) gridPane.getScene().getWindow();
        stage.hide();

    }

}
