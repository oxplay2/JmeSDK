package com.jayfella.sdk.controller;

import com.jayfella.sdk.dialog.CustomDirectoryChooser;
import com.jayfella.sdk.project.DependencyData;
import com.jayfella.sdk.project.Project;
import com.jayfella.sdk.project.newproject.NewProjectCreator;
import com.jayfella.sdk.project.newproject.types.BasicPbrProject;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class NewProjectPage implements Initializable {

    @FXML private TextField nameTextField;
    @FXML private TextField groupIdTextField;
    @FXML private TextField artifactIdTextField;
    @FXML private TextField versionTextField;
    @FXML private TextField locationTextField;

    private MainPage primaryController;
    private Stage welcomeStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        PseudoClass errorClass = PseudoClass.getPseudoClass("error");

        groupIdTextField.textProperty().addListener((observableValue, oldValue, newValue) ->
                groupIdTextField.pseudoClassStateChanged(errorClass, newValue.isBlank()));
    }

    @FXML
    private void browseLocationButtonClicked(ActionEvent event) {

        CustomDirectoryChooser customDirectoryChooser = new CustomDirectoryChooser();
        Path chosenDir = customDirectoryChooser.show();

        if (chosenDir != null) {
            Path newPath = Paths.get(chosenDir.toString(), nameTextField.getText());
            locationTextField.setText(newPath.toString());
        }
    }

    @FXML
    private void createNewProjectButtonClicked(ActionEvent event) {

        // @todo: validate input

        DependencyData dependencyData = new DependencyData(
                groupIdTextField.getText(),
                artifactIdTextField.getText(),
                versionTextField.getText());

        String path = locationTextField.getText();

        NewProjectCreator newProjectCreator = new NewProjectCreator(path, dependencyData);
        newProjectCreator.create(new BasicPbrProject());

        Project.setOpenProject(new Project(path));

        primaryController.start();
        primaryController.getMainStage().show();

        // hide this window
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.hide();

        // also hide the welcome page
        welcomeStage.hide();

        // build the project after we open it.
        primaryController.compileProject();

    }

    public void setPrimaryController(MainPage primaryController) {
        this.primaryController = primaryController;
    }

    public void setWelcomeStage(Stage welcomeStage) {
        this.welcomeStage = welcomeStage;
    }
}
