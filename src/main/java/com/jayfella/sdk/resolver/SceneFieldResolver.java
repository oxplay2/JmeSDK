package com.jayfella.sdk.resolver;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import java.lang.reflect.Field;

public class SceneFieldResolver {

    public static void main(String... args) {

        // create a scene we can use to find objects
        Node scene = new Node("Test Node");

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setName("sunLight");
        scene.addLight(directionalLight);

        scene.attachChild(new Node("myNode"));

        CustomNode customNode = new CustomNode();
        customNode.setName("customNode");
        scene.attachChild(customNode);

        customNode.addControl(new RotateControl());

        scene.attachChild(new Geometry("myGeometry"));

        CustomGeometry customGeometry = new CustomGeometry();
        customGeometry.setName("customGeometry");
        scene.attachChild(customGeometry);

        customGeometry.addControl(new RotateControl());

        // our "controller" class.
        TestClass testClass = new TestClass();

        // resolve the fields to scene objects.
        SceneFieldResolver sceneFieldResolver = new SceneFieldResolver();
        sceneFieldResolver.resolve(testClass, scene);
    }

    public void resolve(Object object, Spatial scene) {

        Field[] fields = object.getClass().getDeclaredFields();

        System.out.println("");
        System.out.println("Before");
        System.out.println("---");

        for (Field field : fields) {

            boolean isAccessible = field.canAccess(object);

            if (!isAccessible) {
                field.setAccessible(true);
            }

            try {
                System.out.println("Field: " + field.getName() + " -> " + field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (!isAccessible) {
                field.setAccessible(false);
            }
        }

        scene.breadthFirstTraversal(new SceneGraphVisitorAdapter() {

            private boolean isAssignable(Field field, Class<?> typeClass) {

                Class<?> clazz = field.getType();
                boolean isAssignable = false;

                while(clazz != null) {

                    if (clazz.isAssignableFrom(typeClass)) {
                        isAssignable = true;
                        break;
                    }

                    clazz = clazz.getSuperclass();
                }

                return isAssignable;
            }

            private void processSpatial(Spatial spatial, Class<? extends Spatial> typeClass) {

                for (Field field : fields) {

                    boolean isAccessible = field.canAccess(object);

                    if (!isAccessible) {
                        field.setAccessible(true);
                    }

                    boolean isAssignable = isAssignable(field, typeClass);

                    if (isAssignable) {

                        if (spatial.getName() != null && spatial.getName().equals(field.getName())) {

                            try {
                                field.set(object, spatial);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    if (!isAccessible) {
                        field.setAccessible(false);
                    }
                }
            }

            private void processControls(Spatial spatial) {

                int controlCount = spatial.getNumControls();

                for (int i = 0; i < controlCount; i++) {
                    Control control = spatial.getControl(i);

                    for (Field controlField : fields) {

                        boolean isAccessible = controlField.canAccess(object);

                        if (!isAccessible) {
                            controlField.setAccessible(true);
                        }

                        boolean isAssignable = isAssignable(controlField, Control.class);

                        if (isAssignable) {

                            if (controlField.getName().equals(spatial.getName() + control.getClass().getSimpleName())) {
                                try {
                                    controlField.set(object, control);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (!isAccessible) {
                            controlField.setAccessible(false);
                        }
                    }
                }
            }

            private void processLights(Spatial spatial) {


                for (Light light : spatial.getLocalLightList()) {

                    for (Field lightField : fields) {

                        boolean isAccessible = lightField.canAccess(object);

                        if (!isAccessible) {
                            lightField.setAccessible(true);
                        }

                        boolean isAssignable = isAssignable(lightField, Light.class);

                        if (isAssignable) {

                            if (light.getName() != null && light.getName().equals(lightField.getName())) {
                                try {
                                    lightField.set(object, light);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (!isAccessible) {
                            lightField.setAccessible(false);
                        }
                    }
                }
            }

            @Override
            public void visit(Geometry geom) {
                processSpatial(geom, Geometry.class);
                processControls(geom);
                processLights(geom);
            }

            public void visit(Node node) {
                processSpatial(node, Node.class);
                processControls(node);
                processLights(node);
            }

        });

        System.out.println("");
        System.out.println("After");
        System.out.println("---");

        for (Field field : fields) {

            boolean isAccessible = field.canAccess(object);

            if (!isAccessible) {
                field.setAccessible(true);
            }

            try {
                System.out.println("Field: " + field.getName() + " -> " + field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (!isAccessible) {
                field.setAccessible(false);
            }
        }

    }

    // a simple example class that extends Node
    public static class CustomNode extends Node {

    }

    // a simple example class that extends Node
    public static class CustomGeometry extends Geometry {

    }

    // a simple control
    public static class RotateControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            getSpatial().rotate(0, tpf, 0);
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {

        }
    }

    public static class TestClass {

        private int testIntField;
        private String testFieldString;

        private Node myNode;
        private CustomNode customNode;

        private Geometry myGeometry;
        private CustomGeometry customGeometry;

        private RotateControl customNodeRotateControl;
        private Control customGeometryRotateControl;

        private DirectionalLight sunLight;

    }

}
