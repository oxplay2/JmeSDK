package com.jayfella.sdk.controller;

import com.jayfella.sdk.core.BindableAppState;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.core.background.BackgroundTask;
import com.jayfella.sdk.core.background.BackgroundTaskListener;
import com.jayfella.sdk.core.tasks.CompileProjectBackgroundTask;
import com.jayfella.sdk.jfx.EditorFxImageView;
import com.jayfella.sdk.project.Project;
import com.jayfella.sdk.sdk.AppStateListViewCell;
import com.jayfella.sdk.service.*;
import com.jme3.app.state.AppState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainPage implements Initializable {

    private static final Logger log = Logger.getLogger(MainPage.class);

    private Stage mainStage;

    @FXML private AnchorPane mainAnchorPane; // keep a reference so we can add the "loading" overlay
    @FXML private TabPane scenesTabPane; //
    @FXML private AnchorPane mainSceneAnchorPane;
    @FXML private Tab sceneEditorTab;
    @FXML private ListView<BindableAppState> appstatesListView;
    @FXML private Accordion inspectorAccordion;
    @FXML private HBox statusHBox;
    @FXML private AnchorPane projectFilesAnchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void start() {

        mainStage.setTitle(Project.getOpenProject().getGradleSettings().getRootProjectName() + " - JmonkeyEngine SDK");

        ServiceManager.registerService(ProjectInjectorService.class);
        ServiceManager.registerService(SceneExplorerService.class);
        ServiceManager.registerService(ProjectExplorerService.class);
        ServiceManager.registerService(new InspectorService(inspectorAccordion));

        ServiceManager.registerService(SceneEditorService.class);
        ServiceManager.registerService(AppStateService.class);
        ServiceManager.registerService(RegistrationService.class);

        sceneEditorTab.setContent(ServiceManager.getService(SceneExplorerService.class).getJfxControl());
        projectFilesAnchorPane.getChildren().add(ServiceManager.getService(ProjectExplorerService.class).getJfxControl());

        createView();
    }

    private void createView() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        // the imageview that will display the JME Scene
        EditorFxImageView imageView = engineService.getImageView();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        AnchorPane.setTopAnchor(stackPane, 0d);
        AnchorPane.setBottomAnchor(stackPane, 0d);
        AnchorPane.setLeftAnchor(stackPane, 0d);
        AnchorPane.setRightAnchor(stackPane, 0d);

        mainSceneAnchorPane.getChildren().add(stackPane);

        stackPane.setFocusTraversable(true);
        mainSceneAnchorPane.setFocusTraversable(true);

        // addControls(mainSceneAnchorPane);
        addSceneEditorControls(mainSceneAnchorPane);

        // background task progress control
        FXMLLoader backgroundTasksLoader = new FXMLLoader(getClass().getResource("/Interface/BackgroundTasksControl.fxml"));

        Parent loaderControl = null;
        try {
            loaderControl = backgroundTasksLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        statusHBox.getChildren().add(loaderControl);

        this.appstatesListView.setCellFactory(p -> new AppStateListViewCell());
        this.appstatesListView.setOnMouseClicked(mouseEvent -> {

            if (mouseEvent.getClickCount() == 2) {

                BindableAppState bindableAppState = appstatesListView.getSelectionModel().getSelectedItem();

                if (bindableAppState != null) {

                    AppStateService appStateService = ServiceManager.getService(AppStateService.class);
                    boolean attached = appStateService.isAttached(bindableAppState.getAppStateClass());

                    if (attached) {
                        AppState appState = engineService.getStateManager().getState(bindableAppState.getAppStateClass());
                        appStateService.detach(appState);

                    }
                    else {

                        try {
                            Constructor<? extends AppState> constructor = bindableAppState.getAppStateClass().getConstructor();
                            AppState appState = constructor.newInstance();

                            ServiceManager.getService(AppStateService.class).attach(appState);

                        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }

                    bindableAppState.enabledProperty().setValue(!attached);

                }

            }

        });

        // changing tabs
        scenesTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {

            switch (newValue.getText()) {
                case "Scene Editor": {
                    ServiceManager.getService(AppStateService.class).disable();
                    ServiceManager.getService(SceneEditorService.class).enable();
                    ServiceManager.getService(SceneExplorerService.class).showHightlight();
                    break;
                }
                case "AppStates": {
                    ServiceManager.getService(SceneEditorService.class).disable();
                    ServiceManager.getService(AppStateService.class).enable();
                    ServiceManager.getService(SceneExplorerService.class).clearHighlight();
                    break;
                }
            }
        });
        // when we open the SDK it will default to the first tab, so attach the editor state.
        ServiceManager.getService(SceneEditorService.class).enable();

        // scenesTabPane.get
    }

    @FXML
    private void buildMenuItemPressed(ActionEvent event) {
        compileProject();
    }

    void compileProject() {

        BackgroundTask compileTask = new CompileProjectBackgroundTask();

        BackgroundTaskListener listener = new BackgroundTaskListener() {

            private Parent root;

            @Override
            public void taskStarted(BackgroundTask task) {

                try {

                    // root = FXMLLoader.load(getClass().getResource("/BuildOverlay.fxml"));
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/BuildOverlay.fxml"));
                    root = fxmlLoader.load();

                    BuildOverlay controller = fxmlLoader.getController();
                    controller.bind(task.statusProperty());

                    controller.getProgressIndicator().visibleProperty().bind(task.succeededProperty());

                    controller.getRebuildButton().setOnAction(event -> {
                        mainAnchorPane.getChildren().remove(root);
                        compileProject();
                    });

                    controller.getRebuildButton().visibleProperty().bind(task.succeededProperty().not());


                    // don't "unmanage" the button because it makes the status jump up.
                    // controller.getRebuildButton().managedProperty().bind(controller.getRebuildButton().visibleProperty());

                    AnchorPane.setLeftAnchor(root, 0d);
                    AnchorPane.setRightAnchor(root, 0d);
                    AnchorPane.setTopAnchor(root, 0d);
                    AnchorPane.setBottomAnchor(root, 0d);

                    mainAnchorPane.getChildren().add(root);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void taskCompleted(BackgroundTask task) {

                if (task.isSucceeded()) {

                    // remove the loading overlay
                    mainAnchorPane.getChildren().remove(root);

                    ServiceManager.getService(ProjectInjectorService.class).inject();
                    ServiceManager.getService(ProjectExplorerService.class).populateProjectFilesTreeView();

                    populateAppStatesListView();
                }
            }
        };

        compileTask.setTaskListener(listener);

        ServiceManager.getService(BackgroundTaskService.class).addTask(compileTask);
    }

    private void populateAppStatesListView() {

        appstatesListView.getItems().clear();

        List<Class<? extends AppState>> appstates = ServiceManager.getService(ProjectInjectorService.class).getAppStates();

        for (Class<? extends AppState> clazz : appstates) {
            BindableAppState bindableAppState = new BindableAppState(clazz);
            appstatesListView.getItems().add(bindableAppState);
        }

    }







    private void addSceneEditorControls(AnchorPane controlsAnchorPane) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Interface/SceneEditorGUI.fxml"));
            controlsAnchorPane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private void onSceneSettingsClicked(ActionEvent actionEvent) {

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/JavaFx/SceneConfiguration.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        // stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Scene Configuration");
        // stage.initOwner(mainStage);
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    private void onExitMenuItemPressed(ActionEvent event) {
        ServiceManager.stop();
        Platform.exit();
    }

    @FXML
    private void runGameMenuItemPressed(ActionEvent event) {



    }

}
