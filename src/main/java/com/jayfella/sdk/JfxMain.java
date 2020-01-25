package com.jayfella.sdk;

import com.jayfella.sdk.config.SdkConfig;
import com.jayfella.sdk.config.UserSettings;
import com.jayfella.sdk.controller.MainPage;
import com.jayfella.sdk.controller.SplashScreen;
import com.jayfella.sdk.core.FolderStructure;
import com.jayfella.sdk.core.LogUtil;
import com.jayfella.sdk.core.tasks.GradleDownloadTask;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.jfx.EditorFxImageView;
import com.jayfella.sdk.jme.EditorCameraState;
import com.jayfella.sdk.service.BackgroundTaskService;
import com.jme3.util.LWJGLBufferAllocator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.lwjgl.system.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class JfxMain extends Application {

    private static final Logger log = LoggerFactory.getLogger(JfxMain.class);

    public static void main(String... args) {

        LogUtil.initializeLogger(Level.DEBUG, true, "%d{dd MMM yyyy HH:mm:ss} [ %p | %c{1} ] %m%n");
        // LogUtil.initializeLogger(Level.DEBUG, true, "%d{dd MMM yyyy HH:mm:ss} [ %p | %c ] %m%n");

        Arrays.stream(new String[] {
                "org.reflections.Reflections"
        }).forEach(p -> LogManager.getLogger(p).setLevel(Level.ERROR));

        // need to disable to work on macos
        Configuration.GLFW_CHECK_THREAD0.set(false);
        // use jemalloc
        Configuration.MEMORY_ALLOCATOR.set("jemalloc");
        // JavaFx
        System.setProperty("prism.lcdtext", "false");

        System.setProperty(LWJGLBufferAllocator.PROPERTY_CONCURRENT_BUFFER_ALLOCATOR, "true");

        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        Platform.setImplicitExit(false);

        FolderStructure.createAll();

        // create the background task service as soon as possible.
        ServiceManager.registerService(BackgroundTaskService.class);

        // check if we have gradle installed.
        SdkConfig sdkConfig = SdkConfig.load();

        if (sdkConfig.getGradleFolder() == null) {
            ServiceManager.getService(BackgroundTaskService.class).addTask(new GradleDownloadTask());
        }

        primaryStage.setTitle("JmonkeyEngine SDK");

        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/JavaFx/SplashScreen.fxml"));
        Parent splashRoot = splashLoader.load();
        SplashScreen splashController = splashLoader.getController();


        // Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(new Scene(splashRoot, 640, 400));
        splashStage.centerOnScreen();

        UserSettings userSettings = UserSettings.load();

        FXMLLoader primaryLoader = new FXMLLoader(getClass().getResource("/JavaFx/MainPage.fxml"));
        Parent root = primaryLoader.load();
        MainPage primaryController = primaryLoader.getController();
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setScene(new Scene(root, userSettings.getWindowWidth(), userSettings.getWindowHeight()));
        primaryStage.setOnHidden(event -> Platform.exit());

        //primaryStage.getScene().getStylesheets().add("/JavaFx/Theme/base.css");

        /*
        ObservableList<String> stylesheets = primaryStage.getScene().getStylesheets();
        stylesheets.add("/ui/css/base.css");
        stylesheets.add("/ui/css/external.css");
        stylesheets.add("/ui/css/custom_ids.css");
        stylesheets.add("/ui/css/custom_classes.css");
        stylesheets.add("/ui/css/dark-color.css");
        */

        primaryController.setMainStage(primaryStage);
        splashController.setPrimaryController(primaryController);
        splashStage.show();

        // window resize and save
        primaryStage.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            userSettings.setWindowWidth(newValue.intValue());
            userSettings.save();
        });

        primaryStage.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            userSettings.setWindowHeight(newValue.intValue());
            userSettings.save();
        });

        // Input handler for JME scene
        primaryStage.getScene().addEventFilter(MouseEvent.ANY, event -> {

            if (event.getTarget() instanceof EditorFxImageView) {

                if (event.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET) {

                    JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

                    engineService
                            .getStateManager()
                            .getState(EditorCameraState.class)
                            .setActiveCamera(engineService.getCamera());

                    engineService.getImageView().requestFocus();
                }
                else if (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) {

                    JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

                    engineService
                            .getStateManager()
                            .getState(EditorCameraState.class)
                            .removeActiveCamera();
                }
            }
        });
    }

}
