package com.jayfella.sdk.jme;

import com.jme3.asset.*;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import javafx.scene.control.Alert;
import javassist.URLClassPath;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Loads files from either a filesystem or jar that may or may not be part of the classpath.
 */

public class ExternalAssetLoader {

    private final List<String> locators = new ArrayList<>();
    private final AssetManager assetManager;

    ExternalAssetLoader(AssetManager assetManager) {
        this.assetManager = assetManager;

    }

    public void registerRoot(String path) {

        if (!locators.contains(path)) {
            locators.add(path);
            assetManager.registerLocator(path, FileLocator.class);
        }
    }

    public <T> T load(String url, Class<T> clazz) {
        return load(new AssetKey(url), clazz);
    }

    public <T> T load(AssetKey assetKey, Class<T> clazz) {

        ExternalFile ext = new ExternalFile(assetKey.getName());

        if (ext.isFile) {

            if (!ext.canRead) {
                /*
                new SimpleTextDialog(
                        "Permission Error",
                        "You do not have permission to read this directory."
                ).show();

                 */

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Permission Error");
                alert.setContentText("You do not have permission to read this directory.");
                alert.show();
                return null;
            }

            assetManager.registerLocator(ext.dir, FileLocator.class);

            // we don't want to keep assets cached that are loaded from external sources.
            // it also causes issues when assets have the same name (e.g. sketchfab uses scene.gltf a lot)
            // this occurs because we register the path, so only the model name is the key.

            if (clazz.isAssignableFrom(Spatial.class)) {

                Spatial model = assetManager.loadModel(ext.name);
                assetManager.unregisterLocator(ext.dir, FileLocator.class);
                assetManager.deleteFromCache((ModelKey) model.getKey());

                return (T) model;
            }
            else if (clazz.isAssignableFrom(Material.class)) {
                Material material = assetManager.loadMaterial(ext.name);
                assetManager.unregisterLocator(ext.dir, FileLocator.class);
                assetManager.deleteFromCache((MaterialKey) material.getKey());

                return (T) material;
            }
            else if (clazz.isAssignableFrom(Texture.class)) {
                Texture texture = assetManager.loadTexture(ext.name);
                assetManager.unregisterLocator(ext.dir, FileLocator.class);
                assetManager.deleteFromCache((TextureKey) texture.getKey());

                return (T)texture;
            }

        }
        else if (ext.isJar) {

            if (clazz.isAssignableFrom(Spatial.class)) {
                try {
                    Spatial model = assetManager.loadModel(ext.fullPath);
                    return (T) model;
                } catch (AssetNotFoundException ex) {

                    File jarFile = new File(ext.jarUrl);

                    addToClasspath(getClass().getClassLoader(), jarFile);
                    Spatial model = assetManager.loadModel(ext.fullPath);
                    removeFromClassPath(getClass().getClassLoader(), jarFile);

                    return (T) model;
                }
            }
            else if (clazz.isAssignableFrom(Material.class)) {
                try {
                    Material material = assetManager.loadMaterial(ext.fullPath);
                    return (T) material;
                } catch (AssetNotFoundException ex) {

                    File jarFile = new File(ext.jarUrl);

                    addToClasspath(getClass().getClassLoader(), jarFile);
                    Material material = assetManager.loadMaterial(ext.fullPath);
                    removeFromClassPath(getClass().getClassLoader(), jarFile);

                    return (T) material;
                }
            }
            else if (clazz.isAssignableFrom(Texture.class)) {

                try {
                    Texture texture = assetManager.loadTexture(ext.fullPath);
                    return (T) texture;
                } catch (AssetNotFoundException ex) {

                    File jarFile = new File(ext.jarUrl);

                    addToClasspath(getClass().getClassLoader(), jarFile);
                    Texture texture = assetManager.loadTexture(ext.fullPath);
                    removeFromClassPath(getClass().getClassLoader(), jarFile);

                    return (T) texture;

                }

            }

        }

        /*
        ApplicationManager.getApplication().invokeLater(() -> {
            new SimpleTextDialog(
                    "Load Resource Error",
                    "Unable to handle: " + assetKey.getName()
            ).show();
        });

         */

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Resource Error");
        alert.setContentText("Unable to handle: " + assetKey.getName());
        alert.show();

        return null;

    }

    private static class ExternalFile {

        private boolean canRead;

        private String name;
        private String dir;
        private String fullPath;

        private String jarUrl;

        private boolean isFile;
        private boolean isJar;

        private static final String fileDelimiter = "file";
        private static final String jarDelimiter = "jar";

        private String removeProtocolDirt(String input) {

            StringBuilder stringBuilder = new StringBuilder(input);

            while (!Character.isLetter(stringBuilder.charAt(0))) {
                stringBuilder.delete(0, 1);
            }

            return stringBuilder.toString();
        }

        ExternalFile(String input) {

            if (input.toLowerCase().startsWith(fileDelimiter)) {
                isFile = true;
                input = input.substring(fileDelimiter.length());
                input = removeProtocolDirt(input);
            }

            else if (input.toLowerCase().startsWith(jarDelimiter)) {
                isJar = true;
                input = input.substring(jarDelimiter.length());
                input = removeProtocolDirt(input);
            }
            else { // there is no "protocol".
                isFile = true;
            }

            if (isFile) {

                File file = new File(input);
                this.canRead = file.canRead();

                dir = file.getParent();
                name = file.getName();
                fullPath = input;
            }
            else if (isJar) {

                String jarPart = "jar!/";
                int index = input.indexOf(jarPart);

                fullPath = input.substring(index + jarPart.length());

                File file = new File(fullPath);
                dir = file.getParent();
                name = file.getName();

                jarUrl = input.substring(0, input.indexOf("!/"));
            }

        }

    }

    private void addToClasspath(ClassLoader classLoader, File file) {
        try {
            URL url = file.toURI().toURL();
            Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    private void removeFromClassPath(ClassLoader classLoader, File file) {

        try {
            URL url = file.toURI().toURL();
            Class<?> urlClass = URLClassLoader.class;
            Field ucpField = urlClass.getDeclaredField("ucp");
            ucpField.setAccessible(true);
            URLClassPath ucp = (URLClassPath) ucpField.get(classLoader);
            Class<?> ucpClass = URLClassPath.class;
            Field urlsField = ucpClass.getDeclaredField("urls");
            urlsField.setAccessible(true);
            Stack urls = (Stack) urlsField.get(ucp);
            urls.remove(url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

}
