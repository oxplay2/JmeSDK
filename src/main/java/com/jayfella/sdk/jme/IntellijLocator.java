package com.jayfella.sdk.jme;

import com.jayfella.sdk.service.JmeEngineService;
import com.jme3.asset.*;
import com.jme3.asset.plugins.UrlAssetInfo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class IntellijLocator implements AssetLocator {

    private String root = "";

    public IntellijLocator(){
    }

    public void setRootPath(String rootPath) {
        this.root = rootPath;
        if (root.equals("/"))
            root = "";
        else if (root.length() > 1){
            if (root.startsWith("/")){
                root = root.substring(1);
            }
            if (!root.endsWith("/"))
                root += "/";
        }
    }

    public AssetInfo locate(AssetManager manager, AssetKey key) {
        URL url;
        String name = key.getName();
        if (name.startsWith("/"))
            name = name.substring(1);

        // intellij doesn't like "/" root paths, it wants a directory, so we "fake" a directory, and then move back
        // out of it to return to normality. It's a little bit of a hack, but it works flawlessly.
        // name = root + "../" + name;
        name = root + name;

        url = JmeEngineService.class.getClassLoader().getResource(name);

        if (url == null) {
            final List<ClassLoader> classLoaders = manager.getClassLoaders();
            for (final ClassLoader classLoader : classLoaders) {
                url = classLoader.getResource(name);
                if(url != null) {
                    break;
                }
            }
        }

        if (url == null)
            return null;

        if (url.getProtocol().equals("file")){
            try {
                String path = new File(url.toURI()).getCanonicalPath();

                // convert to / for windows
                if (File.separatorChar == '\\'){
                    path = path.replace('\\', '/');
                }

                // compare path
                if (!path.endsWith(name)){
                    throw new AssetNotFoundException("Asset name doesn't match requirements.\n"+
                            "\"" + path + "\" doesn't match \"" + name + "\"");
                }
            } catch (URISyntaxException ex) {
                throw new AssetLoadException("Error converting URL to URI", ex);
            } catch (IOException ex){
                throw new AssetLoadException("Failed to get canonical path for " + url, ex);
            }
        }

        try{
            return UrlAssetInfo.create(manager, key, url);
        }catch (IOException ex){
            // This is different handling than URL locator
            // since classpath locating would return null at the getResource()
            // call, otherwise there's a more critical error...
            throw new AssetLoadException("Failed to read URL " + url, ex);
        }
    }

}
