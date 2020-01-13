package com.jayfella.sdk.component;

import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.core.ThreadRunner;
import com.jayfella.sdk.service.JmeEngineService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("rawtypes") // we use Enum a lot
public class EnumComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private ComboBox<Enum> comboBox;

    public EnumComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    public void setEnumValues(Class<? extends Enum> enumData) {
        comboBox.getItems().addAll(enumData.getEnumConstants());
    }

    @Override
    public void load() {
        load("/Interface/Component/Enum.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (getReflectedProperty() != null) {
            Enum enumVal = (Enum) getReflectedProperty().getValue();
            comboBox.getSelectionModel().select(enumVal);
        }

        comboBox.setOnAction(event -> {
            if (getPropertyChangedEvent() != null) {
                Enum value = comboBox.getSelectionModel().getSelectedItem();
                ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(value) );
            }
        });

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Enum: " + name);
    }

    @Override
    public void setValue(Object value) {

        Enum enumValue = (Enum) value;

        Platform.runLater(() -> {
            comboBox.getSelectionModel().select(enumValue);
        });
    }

}
