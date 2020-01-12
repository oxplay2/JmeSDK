package com.jayfella.sdk.core.background;

public interface BackgroundTaskListener {

    void taskStarted(BackgroundTask task);
    void taskCompleted(BackgroundTask task);

}
