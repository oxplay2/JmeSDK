package com.jayfella.sdk.project;

import com.jayfella.sdk.gradle.GradleSettingsParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Project {

    private static Project OPEN_PROJECT;

    private final String projectPath;
    private final GradleSettingsParser gradleSettings;

    public Project(String projectPath) {
        this.projectPath = projectPath;

        this.gradleSettings = new GradleSettingsParser(projectPath);
    }

    public static void setOpenProject(Project project) {
        OPEN_PROJECT = project;
    }

    public static Project getOpenProject() {
        return OPEN_PROJECT;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public GradleSettingsParser getGradleSettings() {
        return gradleSettings;
    }

    public Path getResourcesRoot() {
        return Paths.get(projectPath, "src", "main", "resources");
    }

    public Path getResourcesScenesDirectory() {
        return Paths.get(projectPath, "src", "main", "resources", "Scenes");
    }

}
