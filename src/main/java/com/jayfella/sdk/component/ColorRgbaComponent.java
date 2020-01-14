package com.jayfella.sdk.component;

import com.jayfella.sdk.core.ThreadRunner;
import com.jme3.math.ColorRGBA;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorRgbaComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private ColorPicker colorPicker;

    public ColorRgbaComponent() {
        super();
    }

    public ColorRgbaComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/ColorRgba.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {
            ColorRGBA colorRGBA = (ColorRGBA) getReflectedProperty().getValue();
            colorPicker.setValue(fromColorRGBA(colorRGBA));
        }
        else {
            // if no color is set, I **presume** that all values will be 0
            colorPicker.setValue(fromColorRGBA(ColorRGBA.BlackNoAlpha));
        }

        colorPicker.setOnAction(event -> {

            if (getPropertyChangedEvent() != null) {
                ColorRGBA value = fromColor(colorPicker.getValue());
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }

        });

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("ColorRGBA: " + name);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);

        ColorRGBA colorRGBA = (ColorRGBA) value;

        Platform.runLater(() -> {
            colorPicker.setValue(fromColorRGBA(colorRGBA));
        });
    }

    private ColorRGBA fromColor(Color color) {
        return new ColorRGBA(
                (float) color.getRed(),
                (float) color.getGreen(),
                (float) color.getBlue(),
                (float) color.getOpacity());
    }

    private Color fromColorRGBA(ColorRGBA colorRGBA) {
        return new Color(colorRGBA.r, colorRGBA.g, colorRGBA.b, colorRGBA.a);
    }

}
