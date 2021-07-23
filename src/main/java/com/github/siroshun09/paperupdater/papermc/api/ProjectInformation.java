package com.github.siroshun09.paperupdater.papermc.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class ProjectInformation {

    @SerializedName("project_id")
    @Expose
    private String projectId;

    @SerializedName("project_name")
    @Expose
    private String projectName;

    @SerializedName("version")
    @Expose
    private String version;

    @SuppressWarnings("FieldMayBeFinal")
    @SerializedName("builds")
    @Expose
    private List<Integer> builds;

    private Integer latestBuild;

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getVersion() {
        return version;
    }

    public List<Integer> getBuilds() {
        return builds;
    }

    @Override
    public String toString() {
        return "ProjectInformation{" +
                "projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", version='" + version + '\'' +
                ", builds=" + builds +
                '}';
    }

    public int getLatestBuild() {
        if (latestBuild == null) {
            latestBuild = getBuilds().stream().max(Comparator.naturalOrder()).orElse(-1);
        }

        return latestBuild;
    }
}
