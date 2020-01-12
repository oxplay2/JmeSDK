package com.jayfella.sdk.sdk.list.filter;

import com.jayfella.sdk.component.Component;
import com.jayfella.sdk.component.builder.impl.UniquePropertyBuilder;
import com.jayfella.sdk.controller.list.PostProcessorItem;
import com.jayfella.sdk.core.SelectablePostProcessor;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.service.RegistrationService;
import com.jme3.post.Filter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class FilterCell extends ListCell<SelectablePostProcessor> {

    @Override
    protected void updateItem(final SelectablePostProcessor item, final boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {

            Parent root = null;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/List/PostProcessorItem.fxml"));
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            PostProcessorItem postProcessorItem = fxmlLoader.getController();
            postProcessorItem.getLabel().setText(item.getFriendlyName());
            postProcessorItem.getCheckBox().setSelected(item.isEnabled());
            postProcessorItem.getCheckBox().setOnAction(event -> item.setEnabled(postProcessorItem.getCheckBox().isSelected()));
            postProcessorItem.getConfigButton().disableProperty().bind(item.enabledProperty().not());

            postProcessorItem.getConfigButton().setOnAction(event -> {

                Filter filter = ServiceManager.getService(RegistrationService.class).getFilterRegistration().getFilterInstance(item.getFilterClass());

                if (filter != null) {
                    // UniqueProperties uniqueProperties = new UniqueProperties(filter, "enabled");
                    UniquePropertyBuilder<Filter> builder = new UniquePropertyBuilder<>();
                    builder.setObject(filter);

                    List<Component> components = builder.build();

                    VBox vBox = new VBox();
                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setFitToWidth(true);
                    scrollPane.setContent(vBox);

                    for (Component component : components) {
                        vBox.getChildren().add(component.getJfxControl());
                    }

                    Scene scene = new Scene(scrollPane);
                    Stage stage = new Stage(StageStyle.UTILITY);
                    stage.setScene(scene);

                    stage.show();

                }

            });


            setText(null);
            setGraphic(root);

        }
    }

}
