package com.jayfella.sdk.component.builder.impl;

import com.jayfella.sdk.ext.component.*;
import com.jayfella.sdk.ext.component.builder.ReflectedComponentBuilder;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import javafx.scene.control.TitledPane;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpatialComponentSetBuilder<T> extends AbstractComponentSetBuilder<Spatial> {

    private Spatial object;
    private String[] ignoredProperties = new String[0];

    @Override
    public void setObject(Spatial object) {
        this.object = object;
    }

    @Override
    public void setObject(Spatial object, String... ignoredProperties) {
        this.object = object;
        this.ignoredProperties = ignoredProperties;
    }

    @Override
    public List<TitledPane> build() {

        List<TitledPane> titledPanes = new ArrayList<>();

        try {

            // Transform : location, rotation, scale

            Method localTranslationGetter = object.getClass().getMethod("getLocalTranslation");
            Method localTranslationSetter = object.getClass().getMethod("setLocalTranslation", Vector3f.class);

            Method localRotationGetter = object.getClass().getMethod("getLocalRotation");
            Method localRotationSetter = object.getClass().getMethod("setLocalRotation", Quaternion.class);

            Method localScaleGetter = object.getClass().getMethod("getLocalScale");
            Method localScaleSetter = object.getClass().getMethod("setLocalScale", Vector3f.class);

            Vector3fComponent localTranslation = new Vector3fComponent(object, localTranslationGetter, localTranslationSetter);
            localTranslation.setPropertyName("localTranslation");

            QuaternionComponent localRotation = new QuaternionComponent(object, localRotationGetter, localRotationSetter);
            localRotation.setPropertyName("localRotation");

            Vector3fComponent localScale = new Vector3fComponent(object, localScaleGetter, localScaleSetter);
            localScale.setPropertyName("localScale");

            TitledPane transformPane = createTitledPane("Transform", localTranslation, localRotation, localScale);
            titledPanes.add(transformPane);

            // Spatial: name, cullHint, lastFrustumIntersection, shadowMode, QueueBucket, BatchHint

            Method nameGetter = object.getClass().getMethod("getName");
            Method nameSetter = object.getClass().getMethod("setName", String.class);

            Method cullHintGetter = object.getClass().getMethod("getCullHint");
            Method cullHintSetter = object.getClass().getMethod("setCullHint", com.jme3.scene.Spatial.CullHint.class);

            Method shadowModeGetter = object.getClass().getMethod("getShadowMode");
            Method shadowModeSetter = object.getClass().getMethod("setShadowMode", RenderQueue.ShadowMode.class);

            Method queueBucketGetter = object.getClass().getMethod("getQueueBucket");
            Method queueBucketSetter = object.getClass().getMethod("setQueueBucket", RenderQueue.Bucket.class);

            Method batchHintGetter = object.getClass().getMethod("getBatchHint");
            Method batchHintSetter = object.getClass().getMethod("setBatchHint", com.jme3.scene.Spatial.BatchHint.class);

            StringComponent name = new StringComponent(object, nameGetter, nameSetter);
            name.setPropertyName("name");

            EnumComponent cullHint = new EnumComponent(object, cullHintGetter, cullHintSetter);
            cullHint.setEnumValues(com.jme3.scene.Spatial.CullHint.class);
            cullHint.setPropertyName("cullHint");

            EnumComponent shadowMode = new EnumComponent(object, shadowModeGetter, shadowModeSetter);
            shadowMode.setEnumValues(RenderQueue.ShadowMode.class);
            shadowMode.setPropertyName("shadowMode");

            EnumComponent queueBucket = new EnumComponent(object, queueBucketGetter, queueBucketSetter);
            queueBucket.setEnumValues(RenderQueue.Bucket.class);
            queueBucket.setPropertyName("queueBucket");

            EnumComponent batchHint = new EnumComponent(object, batchHintGetter, batchHintSetter);
            batchHint.setEnumValues(com.jme3.scene.Spatial.BatchHint.class);
            batchHint.setPropertyName("batchHint");

            TitledPane spatialPane = createTitledPane("Spatial", name, cullHint, shadowMode, queueBucket, batchHint);
            titledPanes.add(spatialPane);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (object instanceof Node) {
            // Node doesn't have any properties we want.
            // Leave the comment here so we're aware that we know.
        }
        else if (object instanceof Geometry) {

            try {

                // Geometry-specific data
                Method ignoreTransformGetter = object.getClass().getMethod("isIgnoreTransform");
                Method ignoreTransformSetter = object.getClass().getMethod("setIgnoreTransform", boolean.class);

                Method lodLevelGetter = object.getClass().getMethod("getLodLevel");
                Method lodLevelSetter = object.getClass().getMethod("setLodLevel", int.class);

                BooleanComponent ignoreTranform = new BooleanComponent(object, ignoreTransformGetter, ignoreTransformSetter);
                ignoreTranform.setPropertyName("ignoreTransform");

                IntegerComponent lodLevel = new IntegerComponent(object, lodLevelGetter, lodLevelSetter);
                lodLevel.setPropertyName("lodLevel");

                TitledPane geometryPane = createTitledPane("Geometry", ignoreTranform, lodLevel);

                titledPanes.add(geometryPane);

                // Material
                Method materialGetter = object.getClass().getMethod("getMaterial");
                Method materialSetter = object.getClass().getMethod("setMaterial", Material.class);

                MaterialComponent materialComponent = new MaterialComponent(object, materialGetter, materialSetter);

                TitledPane materialPane = createTitledPane("Material", materialComponent);

                titledPanes.add(materialPane);

                // Additional Render State
                // move it from here to the material titledpane.
                // that way when the material gets updated, the renderstate does too.
                /*
                Geometry geometry = (Geometry) object;
                Material material = geometry.getMaterial();

                ReflectedComponentBuilder<RenderState> renderStateBuilder = new ReflectedComponentBuilder<>();
                renderStateBuilder.setObject(material.getAdditionalRenderState());

                List<Component> renderStateComponents = renderStateBuilder.build();

                TitledPane renderStatePane = createTitledPane("AdditionalRenderState", renderStateComponents);
                titledPanes.add(renderStatePane);

                 */

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }

        String[] ignoredProperties = {
                "localTranslation", "localRotation", "localScale",                                  // transform
                "name", "cullHint", "shadowMode", "queueBucket", "batchHint",                       // spatial
                "material", "ignoreTransform", "dirtyMorph", "lodLevel", "nbSimultaneousGPUMorph",  // Geometry
                "lastFrustumIntersection",                                                          // things we never want
        };

        // for the rest, just use reflection
        // these will be the properties that the user has added if they have extended node.
        ReflectedComponentBuilder<com.jme3.scene.Spatial> reflectedComponentBuilder = new ReflectedComponentBuilder<>();
        reflectedComponentBuilder.setObject(object, ignoredProperties);

        List<Component> reflectedComponents = reflectedComponentBuilder.build();

        if (!reflectedComponents.isEmpty()) {
            TitledPane customTitlePane = createTitledPane("Custom", reflectedComponents);
            titledPanes.add(customTitlePane);
        }

        return titledPanes;
    }


}
