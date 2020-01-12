package com.jayfella.sdk.core;

import com.jayfella.sdk.service.JmeEngineService;
import javafx.application.Platform;

/**
 * Execute code in either the JavaFX or JME thread.
 * Performs a thread check before executing the runnable, and if the tread is not correct, runs in the correct thread.
 *
 * @author jayfella
 *
 */
public class ThreadRunner {

    public static void runInJfxThread(Runnable runnable) {

        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.runLater(runnable);
        }
    }

    public static void runInJmeThread(Runnable runnable) {

        if (JmeEngineService.isJmeThread()) {
            runnable.run();
        }
        else {
            ServiceManager.getService(JmeEngineService.class).enqueue(runnable);
        }

    }

}
