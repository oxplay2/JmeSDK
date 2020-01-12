package com.jayfella.sdk.core;

import javafx.scene.input.DataFormat;

/**
 * Drag and Drop formats to determine what is being dragged/dropped.
 */
public class DnDFormat {

    // We can only have one instance.
    // https://stackoverflow.com/a/38605525

    // serializable objects
    public static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");

    // Scene objects (spatials, etc).
    public static final DataFormat SCENE_OBJECT = new DataFormat("JME Scene Object");

    // project objects (classes, resources, etc)
    public static final DataFormat PROJECT_RESOURCE = new DataFormat("Project Resource");
    public static final DataFormat PROJECT_CLASS = new DataFormat("Project Class");

    // Filters
    public static final DataFormat JME_FILTER = new DataFormat("Jme Filter");

}
