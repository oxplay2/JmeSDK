package com.jayfella.sdk.component;

import com.jayfella.sdk.core.FloatTextFormatter;
import com.jayfella.sdk.core.ThreadRunner;
import com.jme3.math.Vector4f;
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

public class Vector4fComponent extends Component {

    @FXML
    private VBox root;
    @FXML private Label titleLabel;
    @FXML private TextField xTextField;
    @FXML private TextField yTextField;
    @FXML private TextField zTextField;
    @FXML private TextField wTextField;

    public Vector4fComponent() {
        super(null, null, null);
    }

    public Vector4fComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public Parent getJfxControl() {
        return root;
    }

    @Override
    public void load() {
        load("/Interface/Component/Vector4f.fxml");
    }

    private Vector4f getVector4f() {
        float x = Float.parseFloat(xTextField.getText());
        float y = Float.parseFloat(yTextField.getText());
        float z = Float.parseFloat(zTextField.getText());
        float w = Float.parseFloat(wTextField.getText());

        return new Vector4f(x, y, z, w);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {

            Vector4f vector4f = (Vector4f) getReflectedProperty().getValue();

            xTextField.setText("" + vector4f.x);
            yTextField.setText("" + vector4f.y);
            zTextField.setText("" + vector4f.z);
            wTextField.setText("" + vector4f.w);
        }
        else {
            xTextField.setText("" + 0);
            yTextField.setText("" + 0);
            zTextField.setText("" + 0);
            wTextField.setText("" + 0);
        }

        // enforce float values only.
        xTextField.setTextFormatter(new FloatTextFormatter());
        yTextField.setTextFormatter(new FloatTextFormatter());
        zTextField.setTextFormatter(new FloatTextFormatter());
        wTextField.setTextFormatter(new FloatTextFormatter());

        ChangeListener<String> changeListener = (observableValue, oldValue, newValue) -> {

            if (getPropertyChangedEvent() != null) {
                Vector4f value = getVector4f();
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }

        };

        xTextField.textProperty().addListener(changeListener);
        yTextField.textProperty().addListener(changeListener);
        zTextField.textProperty().addListener(changeListener);
        wTextField.textProperty().addListener(changeListener);

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Vector4f: " + name);
    }

    @Override
    public void setValue(Object value) {

        Vector4f vector4f = (Vector4f) value;

        Platform.runLater(() -> {
            xTextField.setText("" + vector4f.x);
            yTextField.setText("" + vector4f.y);
            zTextField.setText("" + vector4f.z);
            wTextField.setText("" + vector4f.w);
        });
    }

}
