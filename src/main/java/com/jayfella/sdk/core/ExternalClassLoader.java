package com.jayfella.sdk.core;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A classloader that allows to to add JAR files to the classpath.
 * Used to inject the built project into the SDK we we can read and use the project classes.
 */
public class ExternalClassLoader extends URLClassLoader {

    public ExternalClassLoader(ClassLoader parent) {
        this(new URL[0], parent);
    }

    public ExternalClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

}
