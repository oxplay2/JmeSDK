package com.jayfella.sdk.component;

import com.jayfella.sdk.core.IntegerTextFormatter;
import com.jayfella.sdk.core.ThreadRunner;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class IntegerComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private TextField textField;

    public IntegerComponent() {
        super();
    }

    public IntegerComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/Integer.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {

            int intValue = (int) getReflectedProperty().getValue();

            textField.setText("" + intValue);
        }
        else {
            textField.setText("" + 0);
        }

        // enforce int values only.
        textField.setTextFormatter(new IntegerTextFormatter());

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
        titleLabel.setText("Integer: " + name);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);

        int intValue = (int) value;

        Platform.runLater(() -> {
            textField.setText("" + intValue);
        });
    }

}
