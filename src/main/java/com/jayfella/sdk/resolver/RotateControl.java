package com.jayfella.sdk.resolver;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class RotateControl extends AbstractControl {

    @Override
    protected void controlUpdate(float tpf) {
        getSpatial().rotate(0, tpf, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
