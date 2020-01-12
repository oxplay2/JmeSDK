package com.jayfella.sdk.project.newproject.types;

public class BasicPbrProject implements ProjectType {


    @Override
    public String getProjectFolder() {
        return "PbrBasic";
    }

    @Override
    public String[] getSources() {
        return new String[] {
                "Main.java"
        };
    }

    @Override
    public String[] getSharedSources() {
        return new String[] {
                "SceneHelper.java"
        };
    }

    @Override
    public String[] getSharedResources() {
        return new String[] {
                "lightprobe.j3o"
        };
    }

}
