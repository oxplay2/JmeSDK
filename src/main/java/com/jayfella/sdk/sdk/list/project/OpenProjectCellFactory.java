package com.jayfella.sdk.sdk.list.project;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.File;

public class OpenProjectCellFactory implements Callback<ListView<File>, ListCell<File>> {

    @Override
    public ListCell<File> call(ListView<File> param) {

        OpenProjectCell cell = new OpenProjectCell();

        return cell;
    }

}
