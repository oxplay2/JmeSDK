package com.jayfella.sdk.dialog;

import com.jayfella.sdk.ext.core.AlphaNumericTextFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CreateFolderDialog {

    private Stage stage;

    @FXML private TextField textField;
    private String folderName = null;

    public boolean showAndWait() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/Dialog/CreateFolder.fxml"));
        fxmlLoader.setController(this);

        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textField.setTextFormatter(new AlphaNumericTextFormatter());
        textField.setOnKeyPressed(this::onKeyPressed);

        Scene scene = new Scene(root);

        stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle("Create Folder");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.showAndWait();

        return folderName != null;
    }

    public String getFolderName() {
        return folderName;
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ENTER)) {

            if (!textField.getText().isBlank()) {
                folderName = textField.getText().trim();
                stage.close();
            }

        }
        else if (event.getCode().equals(KeyCode.ESCAPE)) {
            folderName = null;
            stage.close();
        }

    }

}
