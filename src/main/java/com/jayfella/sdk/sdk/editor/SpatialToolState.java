package com.jayfella.sdk.sdk.editor;

import com.jayfella.sdk.jme.EditorCameraState;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;

public class SpatialToolState extends SpatialTool {

    public enum Tool { Move, Rotate, Scale }

    private SpatialTool moveTool = new SpatialMoveToolState();
    private SpatialTool rotateTool = new SpatialRotateToolState();
    private SpatialTool scaleTool = new SpatialScaleToolState();

    private Tool activeTool;
    private SpatialTool activeToolState;

    public SpatialToolState() {
        activeTool = Tool.Move;
        activeToolState = moveTool;
        activeToolState.setEnabled(true);
    }

    public void setTool(Tool tool) {

        if (this.activeTool == tool) {
            return;
        }

        activeToolState.setEnabled(false);
        Spatial spatial = activeToolState.getSpatial();

        activeTool = tool;

        switch (tool) {
            case Move: activeToolState = moveTool; break;
            case Rotate: activeToolState = rotateTool; break;
            case Scale: activeToolState = scaleTool; break;
        }

        activeToolState.setEnabled(true);
        activeToolState.setSpatial(spatial);
    }

    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        getStateManager().attach(moveTool);
        getStateManager().attach(rotateTool);
        getStateManager().attach(scaleTool);
    }

    @Override
    protected void onDisable() {
        getStateManager().detach(moveTool);
        getStateManager().detach(rotateTool);
        getStateManager().detach(scaleTool);
    }

    public void setSpatial(Spatial spatial) {
        activeToolState.setSpatial(spatial);
    }

    @Override
    public Spatial getSpatial() {
        return activeToolState.getSpatial();
    }

    @Override
    public boolean isBusy() {
        return activeToolState.isBusy();
    }

    @Override
    public void update(float tpf) {
        getState(EditorCameraState.class).setEnabled(!isBusy());
    }

}
