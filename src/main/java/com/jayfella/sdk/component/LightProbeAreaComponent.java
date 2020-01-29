package com.jayfella.sdk.component;

import com.jayfella.sdk.ext.component.Component;
import com.jayfella.sdk.ext.component.builder.ComponentBuilder;
import com.jayfella.sdk.ext.component.builder.ReflectedComponentBuilder;
import com.jme3.light.LightProbe;
import com.jme3.light.ProbeArea;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LightProbeAreaComponent extends Component {

    private LightProbe lightProbe;

    @FXML private ChoiceBox<LightProbe.AreaType> choiceBox;
    @FXML private VBox componentsVBox;

    public LightProbeAreaComponent(LightProbe lightProbe) {
        this.lightProbe = lightProbe;
        load();
    }

    public LightProbeAreaComponent() {
        super();
    }

    public LightProbeAreaComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/JavaFx/Component/LightProbeArea.fxml");
    }

    private void buildProbeComponents(LightProbe lightProbe) {

        choiceBox.getSelectionModel().select(lightProbe.getAreaType());

        componentsVBox.getChildren().clear();

        ComponentBuilder<ProbeArea> componentBuilder = new ReflectedComponentBuilder<>();
        componentBuilder.setObject(lightProbe.getArea(), "areaType");

        List<Component> components = componentBuilder.build();
        componentsVBox.getChildren().addAll(components);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        choiceBox.getItems().addAll(LightProbe.AreaType.values());

        // choiceBox.getSelectionModel().select(lightProbe.getAreaType());
        // buildProbeComponents(lightProbe);

        choiceBox.setOnAction(event -> {
            if (getPropertyChangedEvent() != null) {

                LightProbe.AreaType value = choiceBox.getSelectionModel().getSelectedItem();
                lightProbe.setAreaType(value);

                // When the area type is changes, the ProbeArea is re-created.
                // We need to re-bind so we modify the new ProbeArea.
                buildProbeComponents(lightProbe);
            }
        });
    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        // titleLabel.setText("Material: " + name);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);

        LightProbe lightProbeValue = (LightProbe) value;

        Platform.runLater(() -> {
            buildProbeComponents(lightProbeValue);
        });
    }

}
