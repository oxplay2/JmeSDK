package com.jayfella.sdk.service;

import com.jayfella.sdk.ext.core.Service;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jme3.app.state.AppState;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of AppStates in the users project that can be run (have a noArgs constructor) and whether or not they
 * are actively running.
 * The running states are automatically attached when the AppStates tab is selected.
 * The running states are automatically detached when the AppStates tab is not selected.
 */
public class AppStateService implements Service {

    private final List<AppState> activeAppStates = new ArrayList<>();

    // keep track of the camera position so we can set it back when we switch back to this view.
    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f cameraDirection = new Vector3f();

    public AppStateService() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        cameraPosition.set(engineService.getCamera().getLocation());
        cameraDirection.set(engineService.getCamera().getDirection());

    }

    @Override
    public void stopService() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        for (AppState appState : activeAppStates) {
            engineService.getStateManager().detach(appState);
        }

        activeAppStates.clear();
    }

    public void attach(AppState appState) {

        this.activeAppStates.add(appState);

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        engineService.enqueue(() -> engineService.getStateManager().attach(appState));
    }

    public void detach(AppState appState) {
        this.activeAppStates.remove(appState);

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
        engineService.getStateManager().detach(appState);
    }

    public void setEnabled(boolean enabled) {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        for (AppState appState : activeAppStates) {

            if (enabled) {
                engineService.enqueue(() -> engineService.getStateManager().attach(appState));
            } else {
                engineService.enqueue(() -> engineService.getStateManager().detach(appState));
            }
        }

    }

    public void enable() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {
            engineService.getCamera().setLocation(cameraPosition);
            engineService.getCamera().lookAtDirection(cameraDirection, Vector3f.UNIT_Y);
        });

        setEnabled(true);
    }

    public void disable() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {
            cameraPosition.set(engineService.getCamera().getLocation());
            cameraDirection.set(engineService.getCamera().getDirection());
        });

        setEnabled(false);
    }

    public boolean isAttached(Class<? extends AppState> appstateClass) {
        return ServiceManager.getService(JmeEngineService.class).getStateManager().getState(appstateClass) != null;
    }

}
