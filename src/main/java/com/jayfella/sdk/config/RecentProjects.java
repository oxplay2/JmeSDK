package com.jayfella.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayfella.sdk.core.FolderStructure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecentProjects {

    private static final File recentProjectsFile = new File(FolderStructure.Config.getFolder(), "recent-projects.json");

    private String lastOpenProject = "";
    private List<String> recentProjects = new ArrayList<>();

    public RecentProjects() {

    }

    public String getLastOpenProject() {
        return lastOpenProject;
    }

    public void setLastOpenProject(String lastOpenProject) {
        this.lastOpenProject = lastOpenProject;
    }

    public List<String> getRecentProjects() {
        return recentProjects;
    }

    public void setRecentProjects(List<String> recentProjects) {
        this.recentProjects = recentProjects;
    }

    public static RecentProjects load() {

        if (!recentProjectsFile.exists()) {

            RecentProjects recentProjects = new RecentProjects();
            recentProjects.save();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(recentProjectsFile, RecentProjects.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save() {

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(recentProjectsFile, this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

}
