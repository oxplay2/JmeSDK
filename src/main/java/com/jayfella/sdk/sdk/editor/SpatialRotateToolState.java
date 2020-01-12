package com.jayfella.sdk.sdk.editor;

import com.jme3.app.Application;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;

public class SpatialRotateToolState extends SpatialTool {

    @Override
    protected void initialize(Application app) {

        toolModel = app.getAssetManager().loadModel("Models/SDK/Axis_Rotate.j3o");
        toolModel.setQueueBucket(RenderQueue.Bucket.Transparent); // always visible
        toolModel.breadthFirstTraversal(new SceneGraphVisitorAdapter() {

            @Override
            public void visit(Geometry geom) {
                geom.getMaterial().setBoolean("UseVertexColor", true);
                geom.getMaterial().getAdditionalRenderState().setDepthTest(false);
                geom.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            }

        });
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        if (toolModel.getParent() != null) {
            toolModel.removeFromParent();
        }
    }
}
