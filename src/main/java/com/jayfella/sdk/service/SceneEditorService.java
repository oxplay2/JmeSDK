package com.jayfella.sdk.service;

import com.jayfella.sdk.core.ServiceManager;
import com.jayfella.sdk.core.ThreadRunner;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbe;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class SceneEditorService implements Service {

    private final Node loadedSceneObjectNode = new Node("Loaded Scene Object");

    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private LightProbe lightProbe;

    // true = attach to rootNode
    // false = attach to guiNode;
    private boolean scene3d = true;

    // keep track of the camera position so we can set it back when we switch back to this view.
    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f cameraDirection = new Vector3f();

    public SceneEditorService() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        ambientLight = new AmbientLight();
        directionalLight = new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal());

        Node lightprobeNode = (Node) engineService.getAssetManager().loadModel("Projects/Shared/Resources/lightprobe.j3o");
        lightProbe = (LightProbe) lightprobeNode.getLocalLightList().get(0);

        cameraPosition.set(engineService.getCamera().getLocation());
        cameraDirection.set(engineService.getCamera().getDirection());
    }

    @Override
    public void stopService() {
        loadedSceneObjectNode.removeFromParent();
    }

    public void attachScene(Spatial scene) {

        ThreadRunner.runInJmeThread(() -> {
            loadedSceneObjectNode.detachAllChildren();
            loadedSceneObjectNode.attachChild(scene);
        });

    }

    public void clearScene() {
        ThreadRunner.runInJmeThread(loadedSceneObjectNode::detachAllChildren);
    }

    public void set2d() {
        scene3d = false;
        enable();
    }

    public void set3d() {
        scene3d = true;
        enable();
    }

    public void enable() {
        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {
            engineService.getCamera().setLocation(cameraPosition);
            engineService.getCamera().lookAtDirection(cameraDirection, Vector3f.UNIT_Y);
        });

        if (scene3d) {
            engineService.enqueue(() -> engineService.getRootNode().attachChild(loadedSceneObjectNode));
        }
        else {
            engineService.enqueue(() -> engineService.getGuiNode().attachChild(loadedSceneObjectNode));
        }

        ServiceManager.getService(SceneExplorerService.class).showHighlight();
    }

    public void disable() {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        engineService.enqueue(() -> {
            loadedSceneObjectNode.removeFromParent();

            cameraPosition.set(engineService.getCamera().getLocation());
            cameraDirection.set(engineService.getCamera().getDirection());
        });

        ServiceManager.getService(SceneExplorerService.class).removeHighlight();
    }

    public void setAmbientLightAttached(boolean attached) {
        setLightAttached(ambientLight, attached);
    }

    public void setDirectionalLightAttached(boolean attached) {
        setLightAttached(directionalLight, attached);
    }

    public void setLightProbeAttached(boolean attached) {
        setLightAttached(lightProbe, attached);
    }

    public void setLightAttached(Light lightType, boolean attached) {

        JmeEngineService engineService = ServiceManager.getService(JmeEngineService.class);

        if (attached) {

            engineService.enqueue(() -> {

                boolean added = false;

                for (Light light : loadedSceneObjectNode.getLocalLightList()) {
                    if (light.equals(lightType)) {
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    loadedSceneObjectNode.addLight(lightType);
                }

            });
        }
        else {

            engineService.enqueue(() -> {
                loadedSceneObjectNode.removeLight(lightType);
            });

        }


    }

}
