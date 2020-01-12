package com.jayfella.sdk.controller.list;

import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AppStateListItem implements Initializable {

    @FXML private Label label;
    @FXML private ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setAppStateName(String name) {
        label.setText(name);
    }

    public void bind(ObservableBooleanValue observableBooleanValue) {
        imageView.visibleProperty().bind(observableBooleanValue);
    }

}
