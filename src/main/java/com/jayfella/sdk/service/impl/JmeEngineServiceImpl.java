package com.jayfella.sdk.service.impl;

import com.jayfella.sdk.ext.core.FilterManager;
import com.jayfella.sdk.ext.core.PowerLevel;
import com.jayfella.sdk.ext.graphics.AnistropicFilterAssetListener;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.jfx.EditorFxImageView;
import com.jayfella.sdk.jfx.FrameTransferSceneProcessor;
import com.jayfella.sdk.jfx.ImageViewFrameTransferSceneProcessor;
import com.jayfella.sdk.jfx.JfxMouseInput;
import com.jayfella.sdk.jme.EditorCameraState;
import com.jayfella.sdk.jme.JmeOffscreenSurfaceContext;
import com.jayfella.sdk.sdk.editor.SpatialSelectorState;
import com.jayfella.sdk.sdk.editor.SpatialToolState;
import com.jme3.audio.AudioListenerState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.material.TechniqueDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.AppSettings;
import org.apache.log4j.Logger;

public class JmeEngineServiceImpl extends JmeEngineService {

    private static final Logger log = Logger.getLogger(JmeEngineService.class);
    private static Thread jmeThread = null;

    private JmeOffscreenSurfaceContext canvasContext;
    private FilterManager filterManager = new FilterManager();

    public JmeEngineServiceImpl() {
        super(

        );

        AppSettings settings = new AppSettings(true);
        settings.setCustomRenderer(JmeOffscreenSurfaceContext.class);
        settings.setFrameRate(60);
        // settings.setVSync(true);
        settings.setResizable(true);
        settings.setAudioRenderer(null);
        settings.setUseJoysticks(true);
        settings.setGammaCorrection(true);
        settings.setSamples(16);
        setSettings(settings);
        setPauseOnLostFocus(false);

        setSettings(settings);

        createCanvas();


    }

    @Override
    public void startEngine() {
        canvasContext = (JmeOffscreenSurfaceContext) getContext();
        canvasContext.setSystemListener(this);
        startCanvas(true);
    }

    private boolean initialized = false;

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    private EditorFxImageView imageView;

    private FilterPostProcessor fpp;

    @Override
    public void simpleInitApp() {

        jmeThread = Thread.currentThread();

        stateManager.attachAll(
                new EditorCameraState(),    // our camera movement in the editor
                new EnvironmentCamera(),    // used for probe generation. I'm not certain we want this right now...
                new AudioListenerState(),   // required for positional audio.
                new SpatialSelectorState(),
                new SpatialToolState()      // select & transform spatials.
        );

        // Configure the scene for PBR
        getRenderManager().setPreferredLightMode(TechniqueDef.LightMode.SinglePassAndImageBased);
        getRenderManager().setSinglePassLightBatchSize(10);

        // change the viewport background from black to dark grey.
        // this lets users see things that are black (no light in their scene).
        // it saves a lot of questions about why they can't see anything.
        // @todo let the user specify a color.
        viewPort.setBackgroundColor(new ColorRGBA(0.03f, 0.03f, 0.03f, 1.0f));

        inputManager.setCursorVisible(true);

        // set the initial camera position and direction.
        // back a bit and slightly up because floors are commonly at zero Y and it looks ugly.
        // we're never going to get this right, but for most cases this is fine.
        cam.setLocation(new Vector3f(0, 5, 15));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        // set the default shadow mode for everything.
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // Interesting. I'm not sure how to go about initializing this as a plugin.
        //GuiGlobals.initialize(this);
        //BaseStyles.loadGlassStyle();
        //GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        // initialize and bind the jfx ImageView to render to JFX
        initJavaFxImage();

        // @todo provide an options page to let the users decide what to use.
        // - anistropic filtering, FXAA, etc.
        assetManager.addAssetEventListener(new AnistropicFilterAssetListener(PowerLevel.SIXTEEN));

        // Always set this last. It lets the SDK continue in the knowledge that JME is loaded completely.
        initialized = true;
        log.info("jMonkeyEngine initialized.");
    }

    private void initJavaFxImage() {

        imageView = new EditorFxImageView();
        imageView.getProperties().put(JfxMouseInput.PROP_USE_LOCAL_COORDS, true);
        // imageView.setMouseTransparent(true);
        imageView.setFocusTraversable(true);

        // List<ViewPort> vps = renderManager.getPostViews();
        // ViewPort last = vps.get(vps.size()-1);

        ImageViewFrameTransferSceneProcessor sceneProcessor = new ImageViewFrameTransferSceneProcessor();
        sceneProcessor.bind(imageView, this, viewPort);
        sceneProcessor.setEnabled(true);

        sceneProcessor.setTransferMode(FrameTransferSceneProcessor.TransferMode.ON_CHANGES);
    }

    @Override
    public EditorFxImageView getImageView() {
        return imageView;
    }

    @Override
    public FilterManager getFilterManager() {
        return filterManager;
    }

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void stopService() {
        stop();
    }

    @Override
    public FilterPostProcessor getFilterPostProcessor() {
        return this.fpp;
    }

    /**
     * Removes any existing FilterPostProcessor and adds the given one.
     * Called whenever post processors are added or removed.
     * We require a new FPP because adding a post-processor that requires depth after it's been initialized causes
     * an error if none of the other post processors needed depth.
     * To get around this, we just create a new one with the new post-processors.
     * @param fpp the FilterPostProcessor to display, with all required filters already added.
     */
    @Override
    public void setFilterPostProcessor(FilterPostProcessor fpp) {

        if (this.fpp != null) {
            viewPort.removeProcessor(this.fpp);
        }

        this.fpp = fpp;

        if (this.fpp != null) {
            viewPort.addProcessor(this.fpp);
        }

    }



    /*
    private void getResolutionsLWJGL3() {

        PointerBuffer monitors = GLFW.glfwGetMonitors();

        for ( int i = 0; i < monitors.limit(); i++ ) {
            long monitor = monitors.get(i);

            GLFWVidMode.Buffer modes = GLFW.glfwGetVideoModes(monitor);

            int modeCount = modes.sizeof();

            for ( int j = 0; j < modeCount; j++ ) {
                modes.position(j);

                int width = modes.width();
                int height = modes.height();
                int rate = modes.refreshRate();

                System.out.println("Resolution: " + width + " x " + height + " @ " + rate);

            }
        }
    }

     */

}
