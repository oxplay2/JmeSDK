package com.jayfella.sdk.service.impl;

import com.jayfella.sdk.ext.core.ExternalClassLoader;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jayfella.sdk.ext.service.ProjectInjectorService;
import com.jayfella.sdk.ext.service.RegistrationService;
import com.jayfella.sdk.project.Project;
import com.jme3.app.state.AppState;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ProjectInjectorServiceImpl extends ProjectInjectorService {

    private static final Logger log = Logger.getLogger(ProjectInjectorService.class);

    private ExternalClassLoader externalClassLoader;

    public ProjectInjectorServiceImpl() {
    }

    public ExternalClassLoader getExternalClassLoader() {
        return externalClassLoader;
    }

    public void inject() {
        if (externalClassLoader != null) {
            ServiceManager.getService(JmeEngineService.class).getAssetManager().removeClassLoader(externalClassLoader);
        }

        externalClassLoader = new ExternalClassLoader(ServiceManager.getService(JmeEngineService.class).getClass().getClassLoader());

        URL jarURL = null;

        try {
            jarURL = new File(Project.getOpenProject().getProjectPath(), "./dist/dist.jar").toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        externalClassLoader.addURL(jarURL);

        ServiceManager.getService(JmeEngineService.class).getAssetManager().addClassLoader(externalClassLoader);
        ServiceManager.getService(RegistrationService.class).processRegistrations();

    }


    public List<Class<? extends AppState>> getAppStates() {

        List<Class<? extends AppState>> appstates = new ArrayList<>();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .addClassLoader(externalClassLoader)
                .addUrls(externalClassLoader.getURLs())
                .setScanners(new SubTypesScanner());

        Reflections reflections = new Reflections(builder);

        Set<Class<? extends AppState>> classes = new HashSet<>();

        try {
            classes = reflections.getSubTypesOf(AppState.class);
        }
        catch (Exception ex) {
            // ignore
        }

        // remove any abstract classes. the reflection lib probably has a method to do this.
        classes.removeIf(clazz -> clazz.getSimpleName().toLowerCase().startsWith("abstract"));

        // Sort them alphabetically
        // List<Class<? extends AppState>> sortedClasses = new ArrayList<>(classes);
        // sortedClasses.sort(Comparator.comparing(Class::getSimpleName));

        // appstatesListView.getItems().clear();

        for (Class<? extends AppState> clazz : classes) {

            // System.out.println(clazz.getSimpleName());

            // we don't want anything from "com.jme3"
            if (clazz.getName().startsWith("com.jme3")) {
                continue;
            }

            // only list appstates that have a no-args constructor.
            // we do this because we will attach them, and we don't want any args to add them.
            try {
                Constructor<? extends AppState> constructor = clazz.getConstructor();
                if (constructor == null) {
                    continue;
                }

            } catch (NoSuchMethodException e) {
                // ignore
                // e.printStackTrace();
                continue;
            }

            // BindableAppState bindableAppState = new BindableAppState(clazz);
            // appstatesListView.getItems().add(bindableAppState);
            appstates.add(clazz);
        }

        appstates.sort(Comparator.comparing(Class::getSimpleName));
        return appstates;

    }

    @Override
    public void stopService() {

    }

}
