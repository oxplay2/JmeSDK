package com.jayfella.sdk.core;

import java.io.File;

/**
 * The folder structure of the SDK.
 */
public enum FolderStructure {

    Temporary("./storage/tmp/"),
    Gradle("./storage/gradle/"),
    Config("./config/"),
    Themes("./themes");

    private final String folder;

    FolderStructure(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public static void createAll() {

        for (FolderStructure folder : values()) {
            File file = new File(folder.getFolder());

            if (!file.exists()) {
                file.mkdirs();
            }
        }

    }

}
