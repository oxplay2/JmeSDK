package com.jayfella.sdk.component;

import com.jayfella.sdk.core.ExternalClassLoader;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.core.ThreadRunner;
import com.jayfella.sdk.service.JmeEngineService;
import com.jayfella.sdk.service.ProjectInjectorService;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class Texture2dComponent extends Component {

    @FXML private Label titleLabel;
    @FXML private ComboBox<String> comboBox;
    @FXML private ImageView imageView;

    // Used to populate the reflected texture properties.
    @FXML private VBox propsVBox;

    @FXML private ComboBox<Texture.MinFilter> minFilterComboBox;
    @FXML private ComboBox<Texture.MagFilter> magFilterComboBox;
    @FXML private ComboBox<Texture.WrapMode> wrapModeHorizComboBox;
    @FXML private ComboBox<Texture.WrapMode> wrapModeVertComboBox;

    public Texture2dComponent() {
        super();
    }

    public Texture2dComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public void load() {
        load("/Interface/Component/Texture2d.fxml");
    }

    private Set<String> getResourceImages() {

        ExternalClassLoader externalClassLoader = ServiceManager.getService(ProjectInjectorService.class).getExternalClassLoader();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new ResourcesScanner());

        Reflections reflections = new Reflections(builder);

        Set<String> fileNames = reflections.getResources(Pattern.compile("(?i).*\\.(jpg|gif|png|dds)"));

        return fileNames;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // populate the list of available textures
        Set<String> resourceImages = getResourceImages();
        comboBox.getItems().addAll(resourceImages);

        minFilterComboBox.getItems().addAll(Texture.MinFilter.values());
        magFilterComboBox.getItems().addAll(Texture.MagFilter.values());
        wrapModeHorizComboBox.getItems().addAll(Texture.WrapMode.values());
        wrapModeVertComboBox.getItems().addAll(Texture.WrapMode.values());

        // actually collapse the props VBox if it's hidden, don't just hide it.
        propsVBox.managedProperty().bind(propsVBox.visibleProperty());

        if (getReflectedProperty() != null) {

            Texture2D texture2D = (Texture2D) getReflectedProperty().getValue();

            setValues(texture2D);
        }
        else {
            setValues(null);
        }

        comboBox.setOnAction(event -> {

            if (getPropertyChangedEvent() != null) {

                String resourcePath = comboBox.getSelectionModel().getSelectedItem();

                if (resourcePath != null) {

                    JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
                    Texture2D texture2D = (Texture2D) engineService.getAssetManager().loadTexture(resourcePath);

                    ThreadRunner.runInJmeThread(() -> getPropertyChangedEvent().propertyChanged(texture2D) );

                    setValues(texture2D);
                }

            }

        });

        minFilterComboBox.setOnAction(event -> {
            if (getReflectedProperty() != null) {

                Texture.MinFilter value = minFilterComboBox.getSelectionModel().getSelectedItem();

                if (value != null) {
                    ThreadRunner.runInJmeThread(() -> {
                        Texture2D texture2D = (Texture2D) getReflectedProperty().getValue();
                        texture2D.setMinFilter(value);
                    });
                }
            }
        });

        magFilterComboBox.setOnAction(event -> {
            if (getReflectedProperty() != null) {

                Texture.MagFilter value = magFilterComboBox.getSelectionModel().getSelectedItem();

                if (value != null) {
                    ThreadRunner.runInJmeThread(() -> {
                        Texture2D texture2D = (Texture2D) getReflectedProperty().getValue();
                        texture2D.setMagFilter(value);
                    });
                }
            }
        });

        wrapModeHorizComboBox.setOnAction(event -> {
            if (getReflectedProperty() != null) {

                Texture.WrapMode value = wrapModeHorizComboBox.getSelectionModel().getSelectedItem();

                if (value != null) {
                    ThreadRunner.runInJmeThread(() -> {
                        Texture2D texture2D = (Texture2D) getReflectedProperty().getValue();
                        texture2D.setWrap(Texture.WrapAxis.S, value);
                    });
                }
            }
        });

        wrapModeVertComboBox.setOnAction(event -> {
            if (getReflectedProperty() != null) {

                Texture.WrapMode value = wrapModeHorizComboBox.getSelectionModel().getSelectedItem();

                if (value != null) {
                    ThreadRunner.runInJmeThread(() -> {
                        Texture2D texture2D = (Texture2D) getReflectedProperty().getValue();
                        texture2D.setWrap(Texture.WrapAxis.T, value);
                    });
                }
            }
        });

    }

    private void setValues(Texture2D texture2D) {

        propsVBox.setVisible(texture2D != null);

        if (texture2D != null) {
            String name = texture2D.getKey().getName();
            comboBox.getSelectionModel().select(name);


            minFilterComboBox.getSelectionModel().select(texture2D.getMinFilter());
            magFilterComboBox.getSelectionModel().select(texture2D.getMagFilter());
            wrapModeHorizComboBox.getSelectionModel().select(texture2D.getWrap(Texture.WrapAxis.S));
            wrapModeVertComboBox.getSelectionModel().select(texture2D.getWrap(Texture.WrapAxis.T));

            // imageView.setImage(toImage(texture2D));
            // this is time-consuming and causes some stutter.
            CompletableFuture.supplyAsync(() -> toImage(texture2D))
            .thenApply(result -> {
                Platform.runLater(() -> imageView.setImage(result));
                return null;
            });
        }
        else {
            comboBox.getSelectionModel().clearSelection();
            minFilterComboBox.getSelectionModel().clearSelection();
            magFilterComboBox.getSelectionModel().clearSelection();
            wrapModeHorizComboBox.getSelectionModel().clearSelection();
            wrapModeVertComboBox.getSelectionModel().clearSelection();

            imageView.setImage(null);
        }

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("Texture2D: " + name);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);

        Texture2D texture2D = (Texture2D) value;

        Platform.runLater(() -> {
            setValues(texture2D);
        });
    }

    private Image toImage(Texture2D texture2D) {

        int width = texture2D.getImage().getWidth();
        int height = texture2D.getImage().getHeight();

        ImageRaster imageRaster = ImageRaster.create(texture2D.getImage());
        WritableImage writableImage = new WritableImage(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                ColorRGBA colorRGBA = imageRaster.getPixel(x, (height - 1) - y); // flip Y
                Color color = new Color(colorRGBA.r, colorRGBA.g, colorRGBA.b, colorRGBA.a);
                writableImage.getPixelWriter().setColor(x, y, color);

            }
        }

        return writableImage;
    }

}
