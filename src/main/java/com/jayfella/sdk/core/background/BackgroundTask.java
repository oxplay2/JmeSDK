package com.jayfella.sdk.core.background;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressBar;

public abstract class BackgroundTask implements Runnable {

    private BackgroundTaskListener taskListener;

    private final String name;
    private DoubleProperty progress = new SimpleDoubleProperty(0);
    private StringProperty status = new SimpleStringProperty("");

    public BackgroundTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(double progress) {

        if (progress < 0) progress = ProgressBar.INDETERMINATE_PROGRESS;
        else if (progress > 1) progress = 1d;

        double finalProgress = progress;
        Platform.runLater(() -> this.progress.setValue(finalProgress));

    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public String getStatus() {
        return status.get();
    }

    public synchronized void setStatus(String status) {
        Platform.runLater(() -> this.status.setValue(status));
    }

    public StringProperty statusProperty() {
        return status;
    }

    public BackgroundTaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(BackgroundTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    public void run() {
        if (taskListener != null) {
            Platform.runLater(() -> taskListener.taskStarted(this));
        }

        execute();

        if (taskListener != null) {
            Platform.runLater(() -> taskListener.taskCompleted(this));
        }
    }

    public abstract void execute();

}
