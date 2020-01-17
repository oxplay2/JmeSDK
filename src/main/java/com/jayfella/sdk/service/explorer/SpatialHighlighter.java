package com.jayfella.sdk.service.explorer;

import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;

public class SpatialHighlighter {

    private Geometry highlightGeom;

    public SpatialHighlighter() {

    }

    public void highlightBoundingShape(Spatial spatial) {

        removeHighlight();

        if (spatial.getWorldBound() != null) {
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> {

                if (spatial.getWorldBound() instanceof BoundingBox) {

                    this.highlightGeom = WireBox.makeGeometry((BoundingBox) spatial.getWorldBound());

                    this.highlightGeom.setMaterial(new Material(ServiceManager.getService(JmeEngineService.class).getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
                    this.highlightGeom.getMaterial().getAdditionalRenderState().setLineWidth(2);
                    this.highlightGeom.getMaterial().getAdditionalRenderState().setWireframe(true);
                    this.highlightGeom.getMaterial().setColor("Color", ColorRGBA.Blue);

                    showHightlight();
                }
                else if (spatial.getWorldBound() instanceof BoundingSphere) {

                    BoundingSphere boundingSphere = (BoundingSphere) spatial.getWorldBound();
                    WireSphere wireSphere = new WireSphere(boundingSphere.getRadius());

                    this.highlightGeom = new Geometry("Bounding Sphere Geometry", wireSphere);
                    this.highlightGeom.setMaterial(new Material(ServiceManager.getService(JmeEngineService.class).getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
                    this.highlightGeom.getMaterial().getAdditionalRenderState().setLineWidth(2);
                    this.highlightGeom.getMaterial().getAdditionalRenderState().setWireframe(true);
                    this.highlightGeom.getMaterial().setColor("Color", ColorRGBA.Blue);

                    showHightlight();
                }

            });
        }
    }

    public void highlightMesh(Geometry geometry) {

        removeHighlight();

        if (geometry != null) {
            ServiceManager.getService(JmeEngineService.class).enqueue(() -> {
                this.highlightGeom = new Geometry("Mesh Highlight", geometry.getMesh());
                this.highlightGeom.setLocalRotation(geometry.getWorldRotation());
                this.highlightGeom.setLocalTranslation(geometry.getWorldTranslation());
                this.highlightGeom.setLocalScale(geometry.getWorldScale());

                this.highlightGeom.setMaterial(new Material(ServiceManager.getService(JmeEngineService.class).getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
                this.highlightGeom.getMaterial().getAdditionalRenderState().setLineWidth(2);
                this.highlightGeom.getMaterial().getAdditionalRenderState().setWireframe(true);
                this.highlightGeom.getMaterial().setColor("Color", ColorRGBA.Blue);

                showHightlight();

            });
        }
    }

    public void deleteHighlight() {
        if (highlightGeom != null) {
            ThreadRunner.runInJmeThread(() -> {
                highlightGeom.removeFromParent();
                highlightGeom = null;
            });
        }
    }

    public void removeHighlight() {
        if (highlightGeom != null) {
            ThreadRunner.runInJmeThread(() -> highlightGeom.removeFromParent());

        }
    }

    public void showHightlight() {
        if (highlightGeom != null) {
            JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);
            ThreadRunner.runInJmeThread(() -> {
                engineService.getRootNode().attachChild(highlightGeom);
            });

        }
    }

}
