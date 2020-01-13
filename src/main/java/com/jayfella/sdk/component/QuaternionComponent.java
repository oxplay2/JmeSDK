package com.jayfella.sdk.component;

import com.jayfella.sdk.core.FloatTextFormatter;
import com.jayfella.sdk.core.ThreadRunner;
import com.jme3.math.Quaternion;
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

public class QuaternionComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private TextField xTextField;
    @FXML private TextField yTextField;
    @FXML private TextField zTextField;

    public QuaternionComponent() {
        super(null, null, null);
    }

    public QuaternionComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/Quaternion.fxml");
    }

    private Quaternion getQuaternion() {
        float x = Float.parseFloat(xTextField.getText());
        float y = Float.parseFloat(yTextField.getText());
        float z = Float.parseFloat(zTextField.getText());

        return new Quaternion().fromAngles(x, y, z);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {

            // Vector3f vector3f = (Vector3f) getReflectedProperty().getValue();
            Quaternion quaternion = (Quaternion) getReflectedProperty().getValue();
            float[] euler = quaternion.toAngles(null);

            // setValue() will set it later, we need to set it now.
            // the initialize method is run on the JFX thread, so we're good.
            xTextField.setText("" + euler[0]);
            yTextField.setText("" + euler[1]);
            zTextField.setText("" + euler[2]);
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
                Quaternion value = getQuaternion();
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
        titleLabel.setText("Quaternion: " + name);
    }

    @Override
    public void setValue(Object value) {

        Quaternion quaternion = (Quaternion) value;
        float[] euler = quaternion.toAngles(null);

        Platform.runLater(() -> {
            xTextField.setText("" + euler[0]);
            yTextField.setText("" + euler[1]);
            zTextField.setText("" + euler[2]);
        });
    }

}
