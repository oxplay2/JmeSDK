package com.jayfella.sdk.sdk;

import com.jayfella.sdk.controller.list.AppStateListItem;
import com.jayfella.sdk.core.BindableAppState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class AppStateListViewCell extends ListCell<BindableAppState> {

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(final BindableAppState item, final boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {

            if (fxmlLoader == null) {

                fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/List/AppStateListItem.fxml"));

                try {

                    Parent root = fxmlLoader.load();
                    setText(null);
                    setGraphic(root);

                    AppStateListItem controller = fxmlLoader.getController();
                    controller.setAppStateName(item.getAppStateClass().getSimpleName());
                    controller.bind(item.enabledProperty());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
