package com.jayfella.sdk.sdk.editor;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

public abstract class SpatialTool extends BaseAppState {

    protected Spatial selectedSpatial;
    protected Spatial toolModel;

    protected boolean busy;
    protected float distance;

    public void setSpatial(Spatial spatial) {

        this.selectedSpatial = spatial;

        setEnabled(this.selectedSpatial != null);

        if (selectedSpatial != null && selectedSpatial.getParent() != null) {
            // selectedSpatial.getParent().attachChild(toolModel);
            ((SimpleApplication)getApplication()).getRootNode().attachChild(toolModel);
        }

    }

    public Spatial getSpatial() {
        return selectedSpatial;
    }

    public boolean isBusy() {
        return busy;
    }

    @Override
    public void update(float tpf) {

        Camera camera = getApplication().getCamera();

        // keep the size constant
        distance = camera.getLocation().distance(toolModel.getWorldTranslation());
        toolModel.setLocalScale(distance * .1f);

        if (selectedSpatial != null) {
            toolModel.setLocalTranslation(selectedSpatial.getWorldTranslation());
            toolModel.setLocalRotation(selectedSpatial.getWorldRotation());
        }

    }

}
