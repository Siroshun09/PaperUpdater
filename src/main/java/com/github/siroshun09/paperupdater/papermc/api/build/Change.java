
package com.github.siroshun09.paperupdater.papermc.api.build;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Change {

    @SerializedName("commit")
    @Expose
    private String commit;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("message")
    @Expose
    private String message;

    public String getCommit() {
        return commit;
    }

    public String getSummary() {
        return summary;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Change{" +
                "commit='" + commit + '\'' +
                ", summary='" + summary + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
