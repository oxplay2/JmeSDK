package com.jayfella.sdk.core.tasks;

import com.jayfella.sdk.core.background.BackgroundTask;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.service.SceneExplorerService;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressListener;
import com.jme3.light.LightProbe;
import com.jme3.scene.Spatial;
import javafx.scene.control.TreeItem;

import java.util.concurrent.atomic.AtomicBoolean;

public class LightProbeTask extends BackgroundTask {

    private final Spatial parent;
    private final Spatial scene;
    private final TreeItem<Object> treeItem;

    private final LightProbe.AreaType areaType;
    private final float radius;

    /**
     * Generates a lightprobe.
     * @param parent the spatial to attach the lightProbe
     * @param scene  the scene to generate the environment map.
     */
    public LightProbeTask(Spatial parent, Spatial scene, TreeItem<Object> treeItem, LightProbe.AreaType areaType, float radius) {
        super("Generate LightProbe");

        this.parent = parent;
        this.scene = scene;
        this.treeItem = treeItem;

        this.areaType = areaType;
        this.radius = radius;
    }

    @Override
    public void execute() {

        EnvironmentCamera environmentCamera = ServiceManager.getService(JmeEngineService.class)
                .getStateManager()
                .getState(EnvironmentCamera.class);

        // boolean completed = false;
        AtomicBoolean completed = new AtomicBoolean(false);

        LightProbeFactory.makeProbe(environmentCamera, scene, new JobProgressListener<>() {

            @Override
            public void start() {

            }

            @Override
            public void step(String message) {
                setStatus(message);
            }

            @Override
            public void progress(double value) {
                setProgress(value);
            }

            @Override
            public void done(LightProbe result) {

                result.setAreaType(areaType);
                result.getArea().setRadius(radius);

                parent.addLight(result);

                if (treeItem != null) {
                    ThreadRunner.runInJfxThread(() -> ServiceManager.getService(SceneExplorerService.class).refresh(treeItem));
                }

                completed.set(true);
            }
        });

        while (!completed.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
