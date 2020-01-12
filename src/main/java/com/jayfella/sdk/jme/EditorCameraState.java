package com.jayfella.sdk.jme;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.*;
import com.jme3.input.event.*;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class EditorCameraState extends BaseAppState implements AnalogListener, ActionListener, RawInputListener {

    private Camera cam;

    private final float[] camAngles = new float[3];
    private final Quaternion camRotation = new Quaternion();

    private float panSpeed = 10f;
    private float rotateSpeed = 5f;
    private float zoomSpeed = 10f;

    private boolean lmb_pressed,  mmb_pressed, rmb_pressed;
    private boolean key_forward, key_back, key_left, key_right;
    private boolean key_up, key_down;

    public EditorCameraState() {

    }

    public void setActiveCamera(Camera activeCamera) {
        this.cam = activeCamera;
        this.cam.getRotation().toAngles(camAngles);
    }

    public void removeActiveCamera() {
        this.cam = null;

        lmb_pressed = rmb_pressed = mmb_pressed = false;
        key_forward = key_back = key_left = key_right = false;
        key_up = key_down = false;
    }

    public float getPanSpeed() {
        return panSpeed;
    }

    public void setPanSpeed(float panSpeed) {
        this.panSpeed = panSpeed;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }

    public float getZoomSpeed() {
        return zoomSpeed;
    }

    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }

    @Override
    protected void initialize(Application app) {

        InputManager inputManager = getApplication().getInputManager();

        inputManager.addMapping("MouseAxisX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseAxisX-", new MouseAxisTrigger(MouseInput.AXIS_X, true));

        inputManager.addMapping("MouseAxisY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MouseAxisY-", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addMapping("MouseWheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("MouseWheel-", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        inputManager.addMapping("MouseButtonLeft", new MouseButtonTrigger(0));
        inputManager.addMapping("MouseButtonMiddle", new MouseButtonTrigger(2));
        inputManager.addMapping("MouseButtonRight", new MouseButtonTrigger(1));

        inputManager.addMapping("Key_Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Key_Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Key_Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Key_Right", new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping("Key_Down", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("Key_Up", new KeyTrigger(KeyInput.KEY_E));

        inputManager.addRawInputListener(this);

        inputManager.addListener(this,
                "MouseAxisX", "MouseAxisY",
                "MouseAxisX-", "MouseAxisY-",
                "MouseWheel", "MouseWheel-",
                "MouseButtonLeft", "MouseButtonMiddle", "MouseButtonRight",

                "Key_Forward", "Key_Backward", "Key_Left", "Key_Right",
                "Key_Down", "Key_Up"

        );

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        lmb_pressed = rmb_pressed = mmb_pressed = false;
        key_forward = key_back = key_left = key_right = false;
        key_up = key_down = false;
    }

    @Override
    public void update(float tpf) {

        if (cam == null) {
            return;
        }

        if (lmb_pressed || mmb_pressed || rmb_pressed) {

            if (key_forward) {
                zoomCamera(tpf * zoomSpeed);
            }

            if (key_back) {
                zoomCamera(-tpf * zoomSpeed);
            }

            if (key_left) {
                panCamera(tpf * panSpeed, 0);
            }

            if (key_right) {
                panCamera(-tpf * panSpeed, 0);
            }

            if (key_up) {
                panCamera(0, tpf * panSpeed);
            }

            if (key_down) {
                panCamera(0, -tpf * panSpeed);
            }

        }


    }

    // ActionListener
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

        // System.out.println(name + " : " + isPressed);

        if (cam == null) {
            return;
        }

        if (!isEnabled()) {
            return;
        }

        if (name.equals("MouseButtonLeft")) {
            lmb_pressed = isPressed;
        }

        if (name.equals("MouseButtonMiddle")) {
            mmb_pressed = isPressed;
        }

        if (name.equals("MouseButtonRight")) {
            rmb_pressed = isPressed;
        }


        if (name.equals("Key_Forward")) {
            key_forward = isPressed;
        }

        if (name.equals("Key_Backward")) {
            key_back = isPressed;
        }

        if (name.equals("Key_Left")) {
            key_left = isPressed;
        }

        if (name.equals("Key_Right")) {
            key_right = isPressed;
        }

        if (name.equals("Key_Up")) {
            key_up = isPressed;
        }

        if (name.equals("Key_Down")) {
            key_down = isPressed;
        }

    }

    private void panCamera(float left, float up) {

        Vector3f leftVec = cam.getLeft().mult(left);
        Vector3f upVec = cam.getUp().mult(up);

        Vector3f camLoc = cam.getLocation()
                .add(leftVec)
                .add(upVec);

        cam.setLocation(camLoc);
    }

    private void rotateCamera(float x, float y) {

        // x /= FastMath.TWO_PI;
        // y /= FastMath.TWO_PI;

        //x *= rotateSpeed;
        //y *= rotateSpeed;

        camAngles[0] += x;
        camAngles[1] += y;

        // 89 degrees. Avoid the "flip" problem.
        float maxRotX = FastMath.HALF_PI - FastMath.DEG_TO_RAD;

        // limit camera rotation on the X axis.
        if (camAngles[0] < -maxRotX) {
            camAngles[0] = -maxRotX;
        }

        if (camAngles[0] > maxRotX) {
            camAngles[0] = maxRotX;
        }

        // stop the angles from becoming too big on the Y axis.
        if (camAngles[1] > FastMath.TWO_PI) {
            camAngles[1] -= FastMath.TWO_PI;
        } else if (camAngles[1] < -FastMath.TWO_PI) {
            camAngles[1] += FastMath.TWO_PI;
        }

        camRotation.fromAngles(camAngles);
        cam.setRotation(camRotation);
    }

    private void zoomCamera(float amount) {

        Vector3f camLoc = cam.getLocation();
        Vector3f movement = cam.getDirection().mult(amount);
        Vector3f newLoc = camLoc.add(movement);

        cam.setLocation(newLoc);
    }

    // AnalogListener
    @Override
    public void onAnalog(String name, float value, float tpf) {

        if (cam == null) {
            return;
        }

        if (!isEnabled()) {
            return;
        }

        // rmb = rotate
        // mmb = pan

        switch (name) {

            case "MouseAxisX": {


                if (lmb_pressed || rmb_pressed) {
                    rotateCamera(0, -value * rotateSpeed);
                }

                if (mmb_pressed) {
                    panCamera(value * panSpeed, 0);
                }

                break;
            }

            case "MouseAxisX-": {

                if (lmb_pressed || rmb_pressed) {
                    rotateCamera(0, value * rotateSpeed);
                }

                if (mmb_pressed) {
                    panCamera(-value * panSpeed, 0);
                }

                break;
            }


            case "MouseAxisY": {

                if (lmb_pressed) {
                    zoomCamera(value * zoomSpeed * zoomSpeed);
                }

                if (rmb_pressed) {
                    rotateCamera(-value * rotateSpeed, 0);
                }

                if (mmb_pressed) {
                    panCamera(0, -value * panSpeed);
                }

                break;
            }

            case "MouseAxisY-": {

                if (lmb_pressed) {
                    zoomCamera(-value * zoomSpeed * zoomSpeed);
                }

                if (rmb_pressed) {
                    rotateCamera(value * rotateSpeed, 0);
                }

                if (mmb_pressed) {
                    panCamera(0, value * panSpeed);
                }

                break;
            }


            case "MouseWheel": {
                zoomCamera(value);
                break;
            }

            case "MouseWheel-": {
                zoomCamera(-value);
                break;
            }

        }

    }

    // RawInputListener
    @Override
    public void beginInput() {

    }

    @Override
    public void endInput() {

    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {

    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {

    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {

    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {

    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {

    }

    @Override
    public void onTouchEvent(TouchEvent evt) {

    }
}
