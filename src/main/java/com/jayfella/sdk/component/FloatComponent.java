package com.jayfella.sdk.component;

import com.jayfella.sdk.core.FloatTextFormatter;
import com.jayfella.sdk.core.ThreadRunner;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class FloatComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private TextField textField;

    public FloatComponent() {
        super(null, null, null);
    }

    public FloatComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }


    @Override
    public void load() {
        load("/Interface/Component/Float.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {

           float floatValue = (float) getReflectedProperty().getValue();

            textField.setText("" + floatValue);
        }
        else {
            textField.setText("" + 0);
        }

        // enforce float values only.
        textField.setTextFormatter(new FloatTextFormatter());

        ChangeListener<String> changeListener = (observableValue, oldValue, newValue) -> {

            if (getPropertyChangedEvent() != null) {
                float value = Float.parseFloat(textField.getText());
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }

        };

        textField.textProperty().addListener(changeListener);
    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Float: " + name);
    }

    @Override
    public void setValue(Object value) {

        float floatValue = (float) value;

        Platform.runLater(() -> {
            textField.setText("" + floatValue);
        });
    }

}
