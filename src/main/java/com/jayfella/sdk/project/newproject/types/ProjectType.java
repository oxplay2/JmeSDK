package com.jayfella.sdk.project.newproject.types;

public interface ProjectType {

    String getProjectFolder();

    String[] getSources();

    String[] getSharedSources();
    String[] getSharedResources();

}
