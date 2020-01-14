package com.jayfella.sdk.component;

import com.jayfella.sdk.component.builder.ComponentBuilder;
import com.jayfella.sdk.component.builder.impl.MaterialComponentBuilder;
import com.jayfella.sdk.core.ExternalClassLoader;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.service.JmeEngineService;
import com.jayfella.sdk.service.ProjectInjectorService;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

public class MaterialComponent extends Component {

    // @FXML private Label titleLabel;
    @FXML private ComboBox<String> materialsComboBox;
    @FXML private VBox componentsVBox;

    public MaterialComponent() {
        super();
    }

    public MaterialComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/Material.fxml");
        // initialize(null, null);
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

    private void buildMaterial(Material material) {

        componentsVBox.getChildren().clear();

        ComponentBuilder<Material> materialPropertyBuilder = new MaterialComponentBuilder();
        materialPropertyBuilder.setObject(material);

        List<Component> components = materialPropertyBuilder.build();
        componentsVBox.getChildren().addAll(components);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // materialsComboBox = new ComboBox<>();
        Set<String> resourceMaterials = getResourceMaterials();
        materialsComboBox.getItems().addAll(resourceMaterials);

        if (getReflectedProperty() != null) {
            Material material = (Material) getReflectedProperty().getValue();
            materialsComboBox.getSelectionModel().select(material.getMaterialDef().getAssetName());
            buildMaterial(material);
        }

        materialsComboBox.setOnAction(event -> {

            AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();
            String materialName = materialsComboBox.getSelectionModel().getSelectedItem();

            Material material = null;

            if (materialName.toLowerCase().endsWith(".j3md")) {
                try {
                    material = new Material(assetManager, materialName);
                }
                catch(AssetLoadException e) {

                    Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                    errorAlert.setContentText("AssetLoadException");
                    errorAlert.setHeaderText("Error loading Material Definition");
                    errorAlert.setContentText(e.getMessage());

                    errorAlert.show();
                }
            }
            else if (materialName.toLowerCase().endsWith(".j3m")) {

                try {
                    material = assetManager.loadMaterial(materialName);
                }
                catch(AssetLoadException e) {

                    Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                    errorAlert.setContentText("AssetLoadException");
                    errorAlert.setHeaderText("Error loading Material");
                    errorAlert.setContentText(e.getMessage());

                    errorAlert.show();
                }

            }

            if (material != null) {
                setValue(material);
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

        Material materialValue = (Material) value;
        // float floatValue = (float) value;

        Platform.runLater(() -> {
            // textField.setText("" + floatValue);
            buildMaterial(materialValue);
        });
    }
}
