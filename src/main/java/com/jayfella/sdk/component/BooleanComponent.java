package com.jayfella.sdk.component;

import com.jayfella.sdk.core.ThreadRunner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class BooleanComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private CheckBox checkBox;

    public BooleanComponent() {
        super();
    }

    public BooleanComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/Boolean.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (getReflectedProperty() != null) {
            boolean booleanValue = (boolean) getReflectedProperty().getValue();
            checkBox.setSelected(booleanValue);
        }


        checkBox.setOnAction(event -> {

            if (getPropertyChangedEvent() != null) {
                boolean value = checkBox.isSelected();
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }

        });

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Boolean: " + name);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);

        boolean booleanValue = (boolean) value;

        Platform.runLater(() -> {
            checkBox.setSelected(booleanValue);
        });
    }

}
