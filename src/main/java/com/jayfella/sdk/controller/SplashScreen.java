package com.jayfella.sdk.controller;

import com.jayfella.sdk.config.RecentProjects;
import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.service.JmeEngineService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class SplashScreen implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SplashScreen.class);

    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    private MainPage primaryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        statusLabel.setText("Starting Engine...");
        progressBar.setProgress(-1);

        // LWJGL3 does not return after createCanvas().
        // As a result the JmeEngineService constructor will never complete its creation
        // To get around this we call the createCanvas() method AFTER the constructor.

        new Thread(new ThreadGroup("LWJGL"), () -> {
            ServiceManager.registerService(JmeEngineService.class);
            ServiceManager.getService(JmeEngineService.class).startEngine();
        }, "LWJGL Render").start();

        CompletableFuture
                .runAsync(() -> {

                    // let the engine go through its first iteration of a loop.

                    boolean ready = false;

                    while (!ready) {

                        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

                        if (engineService != null && engineService.isInitialized()) {
                            ready = true;
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                })

                .thenRunAsync(() -> Platform.runLater(() -> progressBar.setProgress(0.5d)))

                .thenRunAsync(() -> {
                    Platform.runLater(() -> {

                        RecentProjects recentProjects = RecentProjects.load();

                        if (!recentProjects.getLastOpenProject().isBlank()) {

                            Stage stage = (Stage) statusLabel.getScene().getWindow();
                            stage.hide();

                            primaryController.start();
                            primaryController.getMainStage().show();

                        }

                        else {

                            FXMLLoader welcomeLoad = new FXMLLoader(getClass().getResource("/JavaFx/WelcomePage.fxml"));
                            Parent root = null;
                            try {
                                root = welcomeLoad.load();

                                WelcomePage welcomeController = welcomeLoad.getController();
                                welcomeController.setPrimaryController(primaryController);

                                Stage stage = (Stage) statusLabel.getScene().getWindow();
                                stage.hide();

                                Stage welcomeStage = new Stage(StageStyle.DECORATED);
                                welcomeStage.setTitle("Welcome to jMonkeyEngine SDK");
                                welcomeStage.setScene(new Scene(root, 780, 490));
                                welcomeStage.getScene().getStylesheets().add("/style.css");
                                welcomeStage.show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                        }

                    });

                })

        ;

    }

    public void setPrimaryController(MainPage primaryController) {
        this.primaryController = primaryController;
    }
}
