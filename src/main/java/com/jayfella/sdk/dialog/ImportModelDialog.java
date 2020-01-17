package com.jayfella.sdk.dialog;

import com.jayfella.sdk.ext.core.AlphaNumericTextFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImportModelDialog implements Initializable {

    private String dirName = "";
    private Stage stage;

    @FXML private TextField textField;

    public boolean show() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/Dialog/ImportModel.fxml"));
        fxmlLoader.setController(this);

        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle("Choose Directory...");
        stage.setScene(scene);

        stage.centerOnScreen();
        stage.showAndWait();

        return !dirName.isBlank();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textField.setTextFormatter(new AlphaNumericTextFormatter());
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ENTER)) {

            TextField textField = (TextField) event.getSource();
            dirName = textField.getText();

            if (!dirName.isBlank()) {
                stage.hide();
            }
        }
        else if (event.getCode().equals(KeyCode.ESCAPE)) {
            dirName = "";
            stage.hide();
        }
    }

    public String getDirectoryName() {
        return dirName;
    }

}
