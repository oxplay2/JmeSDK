package com.jayfella.sdk.core;

import com.jme3.app.state.AppState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * An AppState wrapper that allows us to bind it's attached status.
 * Used in the AppStates tab list.
 */
public class BindableAppState {

    private final Class<? extends AppState> appStateClass;
    private BooleanProperty enabled = new SimpleBooleanProperty();

    public BindableAppState(Class<? extends AppState> appStateClass) {
        this.appStateClass = appStateClass;
    }

    public Class<? extends AppState> getAppStateClass() {
        return appStateClass;
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

}
