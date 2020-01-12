package com.jayfella.sdk.service;

public interface Service {

    /**
     * Called when the ServiceManager is stopped. This only occurs when the application is shutting down.
     */
    void stopService();

}
