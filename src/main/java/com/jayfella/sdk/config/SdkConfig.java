package com.jayfella.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayfella.sdk.core.FolderStructure;

import java.io.File;
import java.io.IOException;

public class SdkConfig {

    private static final File sdkConfigFile = new File(FolderStructure.Config.getFolder(), "sdk-config.json");

    private String gradleFolder;

    public SdkConfig() {

    }

    public String getGradleFolder() {
        return gradleFolder;
    }

    public void setGradleFolder(String gradleFolder) {
        this.gradleFolder = gradleFolder;
    }

    public static SdkConfig load() {

        if (!sdkConfigFile.exists()) {

            SdkConfig sdkConfig = new SdkConfig();
            sdkConfig.save();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(sdkConfigFile, SdkConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save() {

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(sdkConfigFile, this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

}
