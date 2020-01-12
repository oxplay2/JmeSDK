package ${PACKAGE};

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.material.TechniqueDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;

/**
 * A basic game template.
 * @author jayfella
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {

        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("My Awesome Game");
        settings.setSamples(16);
        settings.setGammaCorrection(true);
        app.setSettings(settings);

        app.start();

    }

    private Geometry boxGeometry;

    @Override
    public void simpleInitApp() {

        // Configure the scene for PBR
        getRenderManager().setPreferredLightMode(TechniqueDef.LightMode.SinglePassAndImageBased);
        getRenderManager().setSinglePassLightBatchSize(10);

        // change the viewport background color.
        viewPort.setBackgroundColor(new ColorRGBA(0.03f, 0.03f, 0.03f, 1.0f));

        // Add a simple box.
        Box b = new Box(1, 1, 1);
        boxGeometry = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", ColorRGBA.Blue);
        mat.setFloat("Roughness", 0.2f);
        mat.setFloat("Metallic", 0.0001f);
        boxGeometry.setMaterial(mat);

        rootNode.attachChild(boxGeometry);

        // Add some lights
        DirectionalLight directionalLight = new DirectionalLight(
                new Vector3f(-1, -1, -1).normalizeLocal(),
                new ColorRGBA(1,1,1,1)
        );

        rootNode.addLight(directionalLight);

        SceneHelper sceneHelper = new SceneHelper(assetManager, viewPort, directionalLight);
        sceneHelper.addEffect(SceneHelper.Effect.Ambient_Occlusion);
        sceneHelper.addEffect(SceneHelper.Effect.Bloom);
        sceneHelper.addEffect(SceneHelper.Effect.Directional_Shadows);
        sceneHelper.addEffect(SceneHelper.Effect.ToneMapping);

        // load a lightprobe from test-data
        LightProbe lightProbe = sceneHelper.loadDefaultLightProbe();
        rootNode.addLight(lightProbe);

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code to the update loop
        boxGeometry.rotate(0, tpf * 0.25f, 0);
    }

}