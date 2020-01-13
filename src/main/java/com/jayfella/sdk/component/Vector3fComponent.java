package com.jayfella.sdk.component;

import com.jayfella.sdk.core.FloatTextFormatter;
import com.jayfella.sdk.core.ThreadRunner;
import com.jme3.math.Vector3f;
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

public class Vector3fComponent extends Component implements UpdatableComponent {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private TextField xTextField;
    @FXML private TextField yTextField;
    @FXML private TextField zTextField;

    public Vector3fComponent() {
        super(null, null, null);
    }

    public Vector3fComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public Parent getJfxControl() {
        return root;
    }

    @Override
    public void load() {
        load("/Interface/Component/Vector3f.fxml");
    }

    private Vector3f getVector3f() {
        float x = Float.parseFloat(xTextField.getText());
        float y = Float.parseFloat(yTextField.getText());
        float z = Float.parseFloat(zTextField.getText());

        return new Vector3f(x, y, z);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (getReflectedProperty() != null) {

            Vector3f vector3f = (Vector3f) getReflectedProperty().getValue();

            if (vector3f != null) {
                xTextField.setText("" + vector3f.x);
                yTextField.setText("" + vector3f.y);
                zTextField.setText("" + vector3f.z);
            }
            else {
                xTextField.setText("" + 0);
                yTextField.setText("" + 0);
                zTextField.setText("" + 0);
            }
        }
        else {
            xTextField.setText("" + 0);
            yTextField.setText("" + 0);
            zTextField.setText("" + 0);
        }

        // enforce float values only.
        xTextField.setTextFormatter(new FloatTextFormatter());
        yTextField.setTextFormatter(new FloatTextFormatter());
        zTextField.setTextFormatter(new FloatTextFormatter());

        ChangeListener<String> changeListener = (observableValue, oldValue, newValue) -> {

            if (getPropertyChangedEvent() != null) {
                Vector3f value = getVector3f();
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }

        };

        xTextField.textProperty().addListener(changeListener);
        yTextField.textProperty().addListener(changeListener);
        zTextField.textProperty().addListener(changeListener);

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Vector3f: " + name);
    }

    @Override
    public void setValue(Object value) {

        Vector3f vector3f = (Vector3f) value;

        Platform.runLater(() -> {
            xTextField.setText("" + vector3f.x);
            yTextField.setText("" + vector3f.y);
            zTextField.setText("" + vector3f.z);
        });
    }

    @Override
    public void update() {

        if (getReflectedProperty() != null) {

            Vector3f oldValue = getVector3f();
            Vector3f newValue = (Vector3f) getReflectedProperty().getValue();

            if (!oldValue.equals(newValue)) {
                setValue(newValue);
            }

        }

    }

}
