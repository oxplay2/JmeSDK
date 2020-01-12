package com.jayfella.sdk.registrar;

import com.jayfella.sdk.ext.registrar.filter.FilterRegistrar;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.post.Filter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;

public class DirectionalLightShadowFilterRegistrar extends FilterRegistrar {

    public DirectionalLightShadowFilterRegistrar() {
        setRegisteredClass(DirectionalLightShadowFilter.class);
    }

    @Override
    public Filter createInstance(SimpleApplication application) {
        return new DirectionalLightShadowFilter(application.getAssetManager(), 4096, 4);
    }

    @Override
    public boolean sceneLoaded(Filter filter, Spatial scene) {

        final DirectionalLight[] directionalLight = {null};

        scene.breadthFirstTraversal(spatial -> {

            LightList lightList = spatial.getLocalLightList();

            for (Light light : lightList) {
                if (light instanceof DirectionalLight) {
                    directionalLight[0] = (DirectionalLight) light;
                    break;
                }
            }

        });

        DirectionalLightShadowFilter directionalLightShadowFilter = (DirectionalLightShadowFilter) filter;

        if (directionalLight[0] != null) {
            directionalLightShadowFilter.setLight(directionalLight[0]);
            return true;
        }

        return false;
    }

}
