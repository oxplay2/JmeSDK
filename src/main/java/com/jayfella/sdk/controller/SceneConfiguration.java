package com.jayfella.sdk.controller;

import com.jayfella.sdk.core.FloatTextFormatter;
import com.jayfella.sdk.core.SelectablePostProcessor;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.ext.registrar.filter.FilterRegistrar;
import com.jayfella.sdk.sdk.list.filter.FilterCellFactory;
import com.jayfella.sdk.service.JmeEngineService;
import com.jayfella.sdk.service.RegistrationService;
import com.jayfella.sdk.service.registration.FilterRegistration;
import com.jme3.material.TechniqueDef;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SceneConfiguration implements Initializable {

    @FXML private ComboBox<LightModeType> lightModeComboBox;
    @FXML private TextField lightBatchSizeTextField;
    @FXML private CheckBox gammaCorrectionCheckBox;

    @FXML private TextField nearPlaneTextField;
    @FXML private TextField farPlaneTextField;
    @FXML private TextField fovTextField;

    @FXML private ListView<SelectablePostProcessor> postProcessorsListView;

    // @todo: save this data
    private AtomicReference<Float> fov = new AtomicReference<>(45.0f);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lightModeComboBox.getItems().addAll(LightModeType.values());
        lightModeComboBox.setCellFactory(p -> new LightModeCell());

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        // select the first LightModeType that matches the current lightmode
        // and ifPresent, select it.
        lightModeComboBox.getItems().stream()
                .filter(lm -> lm.lightMode == engineService.getRenderManager().getPreferredLightMode())
                .findFirst()
                .ifPresent(currentType -> lightModeComboBox.getSelectionModel().select(currentType));


        int batchCount = engineService.getRenderManager().getSinglePassLightBatchSize();
        lightBatchSizeTextField.setText("" + batchCount);



        gammaCorrectionCheckBox.setSelected(true);
        gammaCorrectionCheckBox.setOnAction(event -> {
            // java.lang.RuntimeException: No OpenGL context found in the current thread.
            // engineService.getRenderer().setMainFrameBufferSrgb(gammaCorrectionCheckBox.isSelected());
            // engineService.getRenderer().setLinearizeSrgbImages(gammaCorrectionCheckBox.isSelected());
        });

        float nearPlane = engineService.getCamera().getFrustumNear();
        nearPlaneTextField.setText("" + nearPlane);
        nearPlaneTextField.setTextFormatter(new FloatTextFormatter());
        nearPlaneTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {

            Camera cam = engineService.getCamera();

            float aspectRatio = (float)cam.getWidth() / (float)cam.getHeight();
            float near = Float.parseFloat(newValue);

            cam.setFrustumPerspective( fov.get(), aspectRatio, near, cam.getFrustumFar());
        });

        float farPlane = engineService.getCamera().getFrustumFar();
        farPlaneTextField.setText("" + farPlane);
        farPlaneTextField.setTextFormatter(new FloatTextFormatter());
        farPlaneTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {

            Camera cam = engineService.getCamera();

            float aspectRatio = (float)cam.getWidth() / (float)cam.getHeight();
            float far = Float.parseFloat(newValue);

            cam.setFrustumPerspective( fov.get(), aspectRatio, cam.getFrustumNear(), far);

        });

        fovTextField.setText("" + fov.get());
        fovTextField.setTextFormatter(new FloatTextFormatter());
        fovTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {

            Camera cam = engineService.getCamera();

            float aspectRatio = (float)cam.getWidth() / (float)cam.getHeight();
            fov.set(Float.parseFloat(newValue));
            cam.setFrustumPerspective(fov.get(), aspectRatio, cam.getFrustumNear(), cam.getFrustumFar());

        });

        //postProcessorsListView.setCellFactory(p -> new FilterCell());
        postProcessorsListView.setCellFactory(new FilterCellFactory(this));
        populatePostProcessors();
    }


    @FXML
    private void onLightModeComboChanged(ActionEvent actionEvent) {

        LightModeType selectedType = lightModeComboBox.getSelectionModel().getSelectedItem();

        if (selectedType != null) {
            JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
            engineService.getRenderManager().setPreferredLightMode(selectedType.getLightMode());
        }

    }

    public void populatePostProcessors() {

        postProcessorsListView.getItems().clear();

        FilterRegistration filterRegistration = ServiceManager.getService(RegistrationService.class).getFilterRegistration();

        Map<FilterRegistrar, Filter> map = filterRegistration.getRegisteredFilters();

        FilterPostProcessor fpp = ServiceManager.getService(JmeEngineService.class).getFilterPostProcessor();

        List<Class<? extends Filter>> classList = map.keySet().stream().map(FilterRegistrar::getRegisteredClass).collect(Collectors.toList());


        for (Class<? extends Filter> clazz : classList) {
            // System.out.println(clazz.getSimpleName());

            // if there is no FPP, it can't be enabled.
            boolean enabled = fpp != null && fpp.getFilter(clazz) != null;
            SelectablePostProcessor spp = new SelectablePostProcessor(clazz, enabled);

            postProcessorsListView.getItems().add(spp);
        }
    }

    private static class LightModeCell extends ListCell<LightModeType> {

        @Override
        protected void updateItem(final LightModeType item, final boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(null);

                Label label = new Label(item.name());
                Tooltip tooltip = new Tooltip(item.getDescription());

                setGraphic(label);
                setTooltip(tooltip);
            }
        }

    }

    private enum LightModeType {

        Disable(TechniqueDef.LightMode.Disable, "Disable light-based rendering"),

        SinglePass(TechniqueDef.LightMode.SinglePass,
                "Enable light rendering by using a single pass.\n\n" +
                        "An array of light positions and light colors is passed to the shader\n" +
                        "containing the world light list for the geometry being rendered."),

        MultiPass(TechniqueDef.LightMode.MultiPass,
                "Enable light rendering by using multi-pass rendering.\n\n" +
                        "The geometry will be rendered once for each light. Each time the\n" +
                        "light position and light color uniforms are updated to contain\n" +
                        "the values for the current light. The ambient light color uniform\n" +
                        "is only set to the ambient light color on the first pass, future\n" +
                        "passes have it set to black."),

        SinglePassAndImageBased(TechniqueDef.LightMode.SinglePassAndImageBased,
                "Enable light rendering by using a single pass, and also uses Image based lighting for global lighting\n" +
                        "Usually used for PBR\n\n" +
                        "An array of light positions and light colors is passed to the shader\n" +
                        "containing the world light list for the geometry being rendered.\n" +
                        "Light probes are also passed to the shader."
        ),

        StaticPass(TechniqueDef.LightMode.StaticPass, "231");

        private final TechniqueDef.LightMode lightMode;
        private final String description;

        LightModeType(TechniqueDef.LightMode lightMode, String description) {
            this.lightMode = lightMode;
            this.description = description;
        }

        public TechniqueDef.LightMode getLightMode() {
            return lightMode;
        }

        public String getDescription() {
            return description;
        }
    }

}
