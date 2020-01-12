package com.jayfella.sdk.dialog;

import javafx.scene.control.Alert;

public class Alerts {

    public static Alert error(String title, String headerText, String content) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        return alert;
    }

}
