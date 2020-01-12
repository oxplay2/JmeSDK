#### Registering objects into the SDK

The SDK needs to know which spatials, controls and meshes it is allowed to display to the user and how it can create instances of it.
If your object requires no constructor arguments it's just a case of creating a very simple class anywhere in your project.
A convenience class prefixed with NoArgs makes for a very simple registration class.

For a Spatial:
```java
public class MyCustomNodeRegistrar extends NoArgsSpatialRegistrar {
    public MyCustomNodeRegistrar() {
        setRegisteredClass(MyCustomNode.class);
    }
}
```

For a Control:
```java
public class MyCustomControlRegistrar extends NoArgsControlRegistrar {
    public MyCustomControlRegistrar() {
        setRegisteredClass(MyCustomControl.class);
    }
}
```

For a Filter:
```java
public class MyCustomFilterRegistrar extends NoArgsFilterlRegistrar {
    public MyCustomFilterRegistrar() {
        setRegisteredClass(MyCustomFilter.class);
    }
}
```

If your object requires some setup before it can be added to a scene, instead of using the `NoArgs` prefixed class, you would use
the full class. This provides you with a `createInstance` method with a reference to the jMonkey SimpleApplication class so you can
create materials or anything else needed.

```java
public class ParticleEmitterSpatialRegistrar extends SpatialRegistrar {

    public ParticleEmitterSpatialRegistrar() {
        setRegisteredClass(ParticleEmitter.class);
    }

    @Override
    public Spatial createInstance(SimpleApplication application) {

        AssetManager assetManager = application.getAssetManager();

        ParticleEmitter particleEmitter = new ParticleEmitter("New ParticleEmitter", ParticleMesh.Type.Triangle, 30);

        Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));

        particleEmitter.setMaterial(mat_red);
        particleEmitter.setImagesX(2);
        particleEmitter.setImagesY(2); // 2x2 texture animation
        particleEmitter.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        particleEmitter.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        particleEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        particleEmitter.setStartSize(1.5f);
        particleEmitter.setEndSize(0.1f);
        particleEmitter.setGravity(0, 0, 0);
        particleEmitter.setLowLife(1f);
        particleEmitter.setHighLife(3f);
        particleEmitter.getParticleInfluencer().setVelocityVariation(0.3f);

        return particleEmitter;
    }
}
``` 

For Filters, an additional method is available to override:
 ```java
boolean sceneLoaded(Filter filter, Spatial scene)
```

This method is called whenever a scene is loaded or updated in the SDK and returns a boolean to determine whether or not the filter
should be added to the `FilterPostProcessor`. For example, a `DirectionalLightFilter` requires a `DirectionalLight` in the scene. Without
the Light the PostProcessor would throw an exception. The code below shows an example of the DirectionalLightFilter registrar.

```java
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
```

The `sceneLoaded` method checks for an available `DirectionalLight` in the scene, and if it finds one it will attach the light
to the filter and return true, else it will return false, and the filter will not be added to the PostProcessor stack. If at any
time the user does add a DirectionalLight to the scene, the filter will attach itself to that light and thus return true, and add
itself to the FilterPostProcessor stack.

