package com.jayfella.sdk.sdk.editor;

import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.service.SceneExplorerService;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;

public class SpatialSelectorState extends BaseAppState implements ActionListener {

    private final CollisionResults collisionResults = new CollisionResults();
    private final Ray ray = new Ray();

    public SpatialSelectorState() {

    }

    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        InputManager inputManager = getApplication().getInputManager();

        inputManager.addMapping("Select Spatial", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Select Spatial");
    }

    @Override
    protected void onDisable() {

        InputManager inputManager = getApplication().getInputManager();

        inputManager.deleteMapping("Select Spatial");
        inputManager.removeListener(this);
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {

        if (getState(SpatialToolState.class).isBusy()) {
            return;
        }

        if (binding.equals("Select Spatial") && !isPressed) {

            InputManager inputManager = getApplication().getInputManager();
            // JmePanel jmePanel = ServiceManager.getService(JmeEngineService.class).getActivePanel();
            // Camera cam = jmePanel.getCamera();
            Camera cam = getApplication().getCamera();

            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

            ray.setOrigin(click3d);
            ray.setDirection(dir);

            ((SimpleApplication)getApplication()).getRootNode().collideWith(ray, collisionResults);

            if (collisionResults.size() > 0) {
                Geometry geometry = collisionResults.getClosestCollision().getGeometry();
                ServiceManager.getService(SceneExplorerService.class).setSelectedSpatial(geometry);
                collisionResults.clear();
            }

        }

    }

}
