package com.jayfella.sdk.project;

public class DependencyData {

    private String groupId;
    private String artifactId;
    private String Version;

    public DependencyData() {
    }

    public DependencyData(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        Version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }
}
