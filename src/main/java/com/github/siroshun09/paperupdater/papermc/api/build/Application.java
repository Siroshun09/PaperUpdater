
package com.github.siroshun09.paperupdater.papermc.api.build;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Application {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("sha256")
    @Expose
    private String sha256;

    public String getName() {
        return name;
    }

    public String getSha256() {
        return sha256;
    }

    @Override
    public String toString() {
        return "Application{" +
                "name='" + name + '\'' +
                ", sha256='" + sha256 + '\'' +
                '}';
    }
}
