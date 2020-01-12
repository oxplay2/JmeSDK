package com.jayfella.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayfella.sdk.core.FolderStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class UserSettings {

    private static final Logger log = LoggerFactory.getLogger(UserSettings.class);
    private static final File userSettingsFile = new File(FolderStructure.Config.getFolder(), "user-settings.json");

    private int windowWidth = 800;
    private int windowHeight = 600;

    public UserSettings() {
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public static UserSettings load() {

        if (!userSettingsFile.exists()) {

            UserSettings userSettings = new UserSettings();
            userSettings.save();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(userSettingsFile, UserSettings.class);
        } catch (IOException e) {
            // e.printStackTrace();
            log.error("Unable to read UserSettings: " + e.getMessage());
        }

        return null;
    }

    public boolean save() {

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(userSettingsFile, this);
            return true;
        } catch (IOException e) {
            // e.printStackTrace();
            log.error("Unable to save UserSettings: " + e.getMessage());
        }

        return false;

    }

}
