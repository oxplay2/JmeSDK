package com.jayfella.sdk.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GradleSettingsParser {

    private String settingsAsString;

    public GradleSettingsParser(String projectPath) {

        // File file = new File(projectPath, "settings.gradle");
        Path gradleSettingsFilePath = Paths.get(projectPath, "settings.gradle");

        try {
            settingsAsString = new String(Files.readAllBytes(gradleSettingsFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRootProjectName() {
        // rootProject.name = 'JmeSDK'
        String[] lines = settingsAsString.split(System.getProperty("line.separator"));

        for (String line : lines) {
            if (line.startsWith("rootProject.name")) {
                return line.substring(20, line.length() - 2);
            }
        }

        return "Unknown project name!";
    }

}
