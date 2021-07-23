
package com.github.siroshun09.paperupdater.papermc.api.build;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BuildInformation {

    @SerializedName("project_id")
    @Expose
    private String projectId;

    @SerializedName("project_name")
    @Expose
    private String projectName;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("build")
    @Expose
    private long build;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("changes")
    @Expose
    private List<Change> changes = null;

    @SerializedName("downloads")
    @Expose
    private Downloads downloads;

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getVersion() {
        return version;
    }

    public long getBuild() {
        return build;
    }

    public String getTime() {
        return time;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public Downloads getDownloads() {
        return downloads;
    }

    @Override
    public String toString() {
        return "BuildInformation{" +
                "projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", version='" + version + '\'' +
                ", build=" + build +
                ", time='" + time + '\'' +
                ", changes=" + changes +
                ", downloads=" + downloads +
                '}';
    }
}
