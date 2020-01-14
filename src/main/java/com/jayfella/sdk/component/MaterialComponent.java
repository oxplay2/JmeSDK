package com.jayfella.sdk.component;

import com.jayfella.sdk.core.ExternalClassLoader;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.service.JmeEngineService;
import com.jayfella.sdk.service.ProjectInjectorService;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

public class MaterialComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private ComboBox<String> materialsComboBox;

    public MaterialComponent() {
        super();
    }

    public MaterialComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/Material.fxml");
    }

    private Set<String> getResourceMaterials() {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new ResourcesScanner());

        Reflections reflections = new Reflections(builder);

        Set<String> fileNames = reflections.getResources(Pattern.compile("(?i).*\\.(j3md|j3m)"));

        return fileNames;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Set<String> resourceMaterials = getResourceMaterials();
        materialsComboBox.getItems().addAll(resourceMaterials);

        if (getReflectedProperty() != null) {
            Material material = (Material) getReflectedProperty().getValue();
            materialsComboBox.getSelectionModel().select(material.getMaterialDef().getAssetName());
        }

        materialsComboBox.setOnAction(event -> {

            AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();

            String materialName = materialsComboBox.getSelectionModel().getSelectedItem();

            Material material = null;

            if (materialName.toLowerCase().endsWith(".j3md")) {
                material = new Material(assetManager, materialName);

            }
            else if (materialName.toLowerCase().endsWith(".j3m")) {
                material = assetManager.loadMaterial(materialName);
            }

            if (material != null) {
                setValue(material);
            }

        });
    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Material: " + name);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);

        // float floatValue = (float) value;

        Platform.runLater(() -> {
            // textField.setText("" + floatValue);
        });
    }
}
