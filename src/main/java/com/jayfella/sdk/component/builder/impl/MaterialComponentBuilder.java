package com.jayfella.sdk.component.builder.impl;

import com.jayfella.sdk.component.*;
import com.jayfella.sdk.component.builder.ComponentBuilder;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture2D;
import com.jme3.util.ListMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class MaterialComponentBuilder implements ComponentBuilder<Material> {

    private Material material;
    private String[] ignoredProperties = new String[0];

    @Override
    public void setObject(Material object, String... ignoredProperties) {
        this.material = object;
        this.ignoredProperties = ignoredProperties;
    }

    @Override
    public void setObject(Material object) {
        this.material = object;
        this.ignoredProperties = new String[] {
                "ShadowMap0", "ShadowMap1", "ShadowMap2", "ShadowMap3", "ShadowMap4", "ShadowMap5",
                "lightDir", "lightPos"
        };
    }

    @Override
    public List<Component> build() {

        List<Component> components = new ArrayList<>();

        // a list of all possible params
        Collection<MatParam> params = material.getMaterialDef().getMaterialParams();
        List<MatParam> allParams = new ArrayList<>(params);

        allParams.sort(Comparator.comparing(MatParam::getName));

        // a list of params that have been set (either default or by the user).
        ListMap<String, MatParam> setParams = material.getParamsMap();

        for (MatParam matParam : allParams) {

            boolean ignoreProperty = false;

            for (String ignoredProperty: ignoredProperties) {
                if (matParam.getName().equalsIgnoreCase(ignoredProperty)) {
                    ignoreProperty = true;
                    break;
                }
            }

            if (ignoreProperty) {
                continue;
            }

            VarType varType = matParam.getVarType();

            // do them in alphabetical order, just so we keep track easily.

            if (varType == VarType.Boolean) {

                Component component = createComponent(BooleanComponent.class, matParam, setParams);

                if (component != null) {

                    component.setPropertyChangedEvent(value -> {
                        boolean val = (boolean)value;
                        material.setBoolean(matParam.getName(), val);
                    });

                    components.add(component);
                }

            }
            else if (varType == VarType.Float) {

                Component component = createComponent(FloatComponent.class, matParam, setParams);

                if (component != null) {

                    component.setPropertyChangedEvent(value -> {
                        float val = (float)value;
                        material.setFloat(matParam.getName(), val);
                    });

                    components.add(component);
                }

            }
            else if (varType == VarType.Texture2D) {

                Component component = createComponent(Texture2dComponent.class, matParam, setParams);

                if (component != null) {

                    component.setPropertyChangedEvent(value -> {
                        Texture2D val = (Texture2D) value;
                        material.setTexture(matParam.getName(), val);
                    });

                    components.add(component);
                }

            }
            else if (varType == VarType.Vector3) {

                Component component = createComponent(Vector3fComponent.class, matParam, setParams);

                if (component != null) {

                    component.setPropertyChangedEvent(value -> {
                        Vector3f val = (Vector3f)value;
                        material.setVector3(matParam.getName(), val);
                    });

                    components.add(component);
                }

            }
            else if (varType == VarType.Vector4) {

                // a Vector4f could also be a ColorRGBA.

                if (matParam.getValue() instanceof Vector4f) {
                    Component component = createComponent(Vector4fComponent.class, matParam, setParams);

                    if (component != null) {

                        component.setPropertyChangedEvent(value -> {
                            Vector4f val = (Vector4f)value;
                            material.setVector4(matParam.getName(), val);
                        });

                        components.add(component);
                    }
                }
                else {

                    Component component = createComponent(ColorRgbaComponent.class, matParam, setParams);

                    if (component != null) {

                        component.setPropertyChangedEvent(value -> {
                            ColorRGBA val = (ColorRGBA)value;
                            material.setColor(matParam.getName(), val);
                        });

                        components.add(component);
                    }
                }

            }

        }

        return components;
    }

    private Component createComponent(Class<? extends Component> componentClass, MatParam matParam, ListMap<String, MatParam> setParams) {

        Component component;

        try {
            Constructor<? extends Component> constructor = componentClass.getConstructor();
            component = constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        // Component component = new BooleanComponent();
        // component.load();
        component.setPropertyName(matParam.getName());

        // set the value of the component if one is found.
        setParams.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(matParam.getName()))
                .findFirst()
                .ifPresent(setParam -> component.setValue(setParam.getValue().getValue()));

        return component;
    }

}
