package com.jayfella.sdk.component;

import com.jayfella.sdk.core.ThreadRunner;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class StringComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private TextField textField;

    public StringComponent() {
        super(null, null, null);
    }

    public StringComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/String.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {

            String stringValue = (String) getReflectedProperty().getValue();

            textField.setText(stringValue);
        }

        ChangeListener<String> changeListener = (observableValue, oldValue, newValue) -> {

            if (getPropertyChangedEvent() != null) {
                String value = textField.getText();
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }

        };

        textField.textProperty().addListener(changeListener);
    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("String: " + name);
    }

    @Override
    public void setValue(Object value) {

        String stringValue = (String) value;

        Platform.runLater(() -> {
            textField.setText(stringValue);
        });
    }

}
