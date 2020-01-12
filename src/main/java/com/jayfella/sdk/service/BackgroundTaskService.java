package com.jayfella.sdk.service;

import com.jayfella.sdk.core.background.BackgroundTask;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTaskService implements Service {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ObservableList<BackgroundTask> tasks = FXCollections.observableArrayList();

    private final DoubleProperty progressTotal = new SimpleDoubleProperty(0);
    private final StringProperty status = new SimpleStringProperty("");

    private final Timer timer = new Timer();

    public BackgroundTaskService() {
        timer.scheduleAtFixedRate(new UpdateLoop(), 200, 200);
    }

    public void addTask(BackgroundTask task) {

        Platform.runLater(() -> {

            tasks.add(task);

            CompletableFuture
                    .runAsync(task, executorService)
                    .thenRun(() -> Platform.runLater(() -> tasks.remove(task)));
        });
    }

    public double getProgressTotal() {
        return progressTotal.get();
    }

    public DoubleProperty progressTotalProperty() {
        return progressTotal;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public ObservableList<BackgroundTask> getTasks() {
        return tasks;
    }

    @Override
    public void stopService() {
        timer.cancel();
        timer.purge();

        executorService.shutdownNow();
    }

    private class UpdateLoop extends TimerTask {

        @Override
        public void run() {

            // display a singular value if there's only one task.
            // this will have the benefit of also displaying "indeterminate" on a single task

            // if multiple tasks are running, only get the progress of those are are determinate.

            Platform.runLater(() -> {

                if (tasks.isEmpty()) {
                    progressTotal.setValue(0);
                    statusProperty().setValue("");
                }
                else if (tasks.size() == 1) {

                    // if there's only one, just display that value.
                    BackgroundTask task = tasks.get(0);
                    double progress = task.getProgress();

                    if (progress == 0) {
                        progressTotal.setValue(-1);
                    }
                    else {
                        progressTotal.setValue(progress);
                    }

                    statusProperty().setValue(task.getStatus());

                }
                else {

                    // some tasks are "indeterminate", so we ignore those.

                    double currentProgressTotal = 0;
                    double divisor = 0;

                    for (BackgroundTask task : tasks) {

                        double progress = task.getProgress();

                        if (progress >= 0) {
                            currentProgressTotal += progress;
                            divisor++;
                        }

                    }

                    if (currentProgressTotal == 0 && tasks.size() > 0) {
                        progressTotal.setValue(-1);
                    }
                    else {
                        progressTotal.setValue(currentProgressTotal / divisor);
                    }

                    statusProperty().setValue("Processing " + tasks.size() + " tasks...");
                }

            });

        }

    }

}
