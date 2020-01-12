package com.jayfella.sdk.controller;

import com.google.common.io.Files;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FileIcon implements Initializable {

    @FXML private ImageView imageView;
    @FXML private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setFile(File file) {

        String extension = Files.getFileExtension(file.getName()).toLowerCase();

        // Image image;
        String resourceImage;

        if (file.isDirectory()) {
            resourceImage = "/Icons/File/folder-solid.png";
        }
        else {
            switch (extension) {
                default: resourceImage = "/Icons/File/file-regular.png";
            }
        }

        imageView.setImage(new Image(resourceImage));
        label.setText(file.getName());

    }

}
