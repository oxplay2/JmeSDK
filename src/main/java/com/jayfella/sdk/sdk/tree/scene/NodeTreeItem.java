package com.jayfella.sdk.sdk.tree.scene;

import com.jayfella.sdk.core.background.BackgroundTask;
import com.jayfella.sdk.core.tasks.LightProbeTask;
import com.jayfella.sdk.dialog.NewLightProbeDialog;
import com.jayfella.sdk.dialog.NewSkyBoxDialog;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.core.ThreadRunner;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.service.BackgroundTaskService;
import com.jayfella.sdk.service.SceneExplorerService;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.*;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.io.File;

public class NodeTreeItem extends SceneTreeItem {

    public NodeTreeItem(Node node) {
        super(node, new FontAwesomeIconView(FontAwesomeIcon.CODE_FORK));
    }

    @Override
    public ContextMenu getMenu() {

        Spatial item = (Spatial) getValue();
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE));

        deleteItem.setOnAction(event -> {
            ThreadRunner.runInJmeThread(item::removeFromParent);
            getParent().getChildren().remove(this);
        });
        contextMenu.getItems().add(deleteItem);

        contextMenu.getItems().add(new SeparatorMenuItem());

        MenuItem skyBoxMenuItem = new MenuItem("SkyBox From Image...", new FontAwesomeIconView(FontAwesomeIcon.SKYATLAS));
        skyBoxMenuItem.setOnAction(event -> {

            NewSkyBoxDialog newSkyBoxDialog = new NewSkyBoxDialog();
            File file = newSkyBoxDialog.showAndWait();

            if (file != null) {


                AssetManager assetManager = ServiceManager.getService(JmeEngineService.class).getAssetManager();
                assetManager.registerLocator(file.getParent(), FileLocator.class);

                // ModelKey modelKey = new ModelKey(file.getName());
                // Node model = (Node) ServiceManager.getService(JmeEngineService.class).getAssetManager().loadModel(modelKey);
                TextureKey textureKey = new TextureKey(file.getName());
                Texture texture = assetManager.loadTexture(textureKey);

                assetManager.unregisterLocator(file.getParent(), FileLocator.class);

                Geometry sky = (Geometry) SkyFactory.createSky(assetManager, texture, SkyFactory.EnvMapType.EquirectMap);
                sky.setName("Sky");
                sky.setShadowMode(RenderQueue.ShadowMode.Off); // !! important for shadows to work on the rest of the scene !!.

                // There's a bug in the LightProbe generator where you can only pass a Node to the generator.
                // As a work-around, we'll add the sky geometry to a Node so the user can select the sky node to generate a probe.

                Node skyNode = new Node("Sky");
                skyNode.attachChild(sky);

                Node node = (Node) getValue();
                ThreadRunner.runInJmeThread(() -> {
                    node.attachChild(skyNode);

                    // refresh *after* the item has been added to the scene.
                    ThreadRunner.runInJfxThread(() -> ServiceManager.getService(SceneExplorerService.class).refresh(this));
                });

            }

        });
        contextMenu.getItems().add(skyBoxMenuItem);

        contextMenu.getItems().add(new SeparatorMenuItem());

        Menu lightMenu = new Menu("Add Light");
        contextMenu.getItems().add(lightMenu);

        MenuItem ambientLightItem = new MenuItem("Ambient Light", new FontAwesomeIconView(FontAwesomeIcon.SUN_ALT));
        ambientLightItem.setOnAction(event -> {

            ThreadRunner.runInJmeThread(() -> {

                item.addLight(new AmbientLight());

                // refresh *after* the item has been added to the scene.
                ThreadRunner.runInJfxThread(() -> ServiceManager.getService(SceneExplorerService.class).refresh(this));
            });

        });
        lightMenu.getItems().add(ambientLightItem);

        MenuItem directionalLightItem = new MenuItem("Directional Light", new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
        directionalLightItem.setOnAction(event -> {

            ThreadRunner.runInJmeThread(() -> {

                item.addLight(new DirectionalLight());

                // refresh *after* the item has been added to the scene.
                ThreadRunner.runInJfxThread(() -> ServiceManager.getService(SceneExplorerService.class).refresh(this));
            });

        });
        lightMenu.getItems().add(directionalLightItem);

        MenuItem pointLightItem = new MenuItem("Point Light", new FontAwesomeIconView(FontAwesomeIcon.LIGHTBULB_ALT));
        pointLightItem.setOnAction(event -> {

            ThreadRunner.runInJmeThread(() -> {

                item.addLight(new PointLight());

                // refresh *after* the item has been added to the scene.
                ThreadRunner.runInJfxThread(() -> ServiceManager.getService(SceneExplorerService.class).refresh(this));
            });

        });
        lightMenu.getItems().add(pointLightItem);

        MenuItem spotLightMenuItem = new MenuItem("Spot Light", new FontAwesomeIconView(FontAwesomeIcon.ARROW_RIGHT));
        spotLightMenuItem.setOnAction(event -> {

            ThreadRunner.runInJmeThread(() -> {

                item.addLight(new SpotLight());

                // refresh *after* the item has been added to the scene.
                ThreadRunner.runInJfxThread(() -> ServiceManager.getService(SceneExplorerService.class).refresh(this));
            });

        });
        lightMenu.getItems().add(spotLightMenuItem);

        MenuItem lightProbeMenuItem = new MenuItem("Light Probe", new FontAwesomeIconView(FontAwesomeIcon.DOT_CIRCLE_ALT));
        lightProbeMenuItem.setOnAction(event -> {

            NewLightProbeDialog newLightProbeDialog = new NewLightProbeDialog();
            Node selectedNode = newLightProbeDialog.showAndWait();

            if (selectedNode != null) {

                LightProbe.AreaType areaType = newLightProbeDialog.getSelectedAreaType();
                float radius = newLightProbeDialog.getSelectedRadius();

                BackgroundTask backgroundTask = new LightProbeTask(item, selectedNode, this, areaType, radius);
                ServiceManager.getService(BackgroundTaskService.class).addTask(backgroundTask);

            }

        });

        lightMenu.getItems().add(lightProbeMenuItem);

        return contextMenu;
    }
}
