package com.jayfella.sdk.component.control;

import com.jayfella.sdk.component.Component;
import com.jayfella.sdk.component.DisposableComponent;
import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.action.Action;
import com.jme3.scene.Spatial;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class AnimComposerComponent extends Component implements DisposableComponent {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private ComboBox<AnimClip> animsComboBox;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Button stopButton;
    @FXML private Slider timeSlider;
    @FXML private Slider speedSlider;

    private AnimComposer animComposer;
    private AnimClip animClip;
    private Action action;

    public AnimComposerComponent() {
        super(null, null, null);
    }

    public AnimComposerComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
    }

    @Override
    public Parent getJfxControl() {
        return root;
    }

    @Override
    public void load() {
        load("/Interface/Component/Control/AnimComposer.fxml");
    }

    private void setValues(AnimComposer animComposer) {

        this.animComposer = animComposer;

        Collection<AnimClip> animClipsCollection = animComposer.getAnimClips();
        animsComboBox.getItems().addAll(animClipsCollection);

        if (!animClipsCollection.isEmpty()) {
            animsComboBox.getSelectionModel().selectFirst();
            animClip = animsComboBox.getSelectionModel().getSelectedItem();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (getReflectedProperty() != null) {

            AnimComposer animComposer = (AnimComposer) getReflectedProperty().getValue();
            setValues(animComposer);

            // boolean booleanValue = (boolean) getReflectedProperty().getValue();
            // checkBox.setSelected(booleanValue);
        }

        animsComboBox.setCellFactory(p -> new AnimClipCell());
        animsComboBox.setOnAction(event -> {

            AnimClip selectedClip = animsComboBox.getSelectionModel().getSelectedItem();

            if (selectedClip != null) {
                animClip = selectedClip;
                timeSlider.setMax(animClip.getLength());

                action = animComposer.setCurrentAction(animClip.getName());
                action.setSpeed(speedSlider.getValue());
            }

        });

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            animComposer.setTime(AnimComposer.DEFAULT_LAYER, newValue.doubleValue());
        });

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (action != null) {
                action.setSpeed(speedSlider.getValue());
            }

        });

        playButton.setOnAction(event -> {
            action = animComposer.setCurrentAction(animClip.getName());
            action.setSpeed(speedSlider.getValue());
        });

        pauseButton.setOnAction(event -> {
            if (action != null) {
                action.setSpeed(0);
            }
        });

        stopButton.setOnAction(event -> {
            animComposer.reset();

            // this should always be true but you never know.
            Spatial spatial = animComposer.getSpatial();

            if (spatial != null) {
                SkinningControl skinningControl = spatial.getControl(SkinningControl.class);

                if (skinningControl != null) {
                    skinningControl.getArmature().applyBindPose();
                }
            }

        });

    }

    @Override
    public void setPropertyName(String name) {
        super.setPropertyName(name);
        titleLabel.setText("AnimComposer");
    }

    @Override
    public void setValue(Object value) {

        AnimComposer animComposer = (AnimComposer) value;

        Platform.runLater(() -> {
            setValues(animComposer);
        });
    }

    @Override
    public void dispose() {
        animComposer.reset();

        // this should always be true but you never know.
        Spatial spatial = animComposer.getSpatial();

        if (spatial != null) {
            SkinningControl skinningControl = spatial.getControl(SkinningControl.class);

            if (skinningControl != null) {
                skinningControl.getArmature().applyBindPose();
            }
        }
    }

    private class AnimClipCell extends ListCell<AnimClip> {

        @Override
        public void updateItem(AnimClip item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            }
            else {

                setText(item.getName());
                setGraphic(null);

            }

        }
    }

}
