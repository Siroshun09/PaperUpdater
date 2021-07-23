
package com.github.siroshun09.paperupdater.papermc.api.build;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Downloads {

    @SerializedName("application")
    @Expose
    private Application application;

    public Application getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return "Downloads{" +
                "application=" + application +
                '}';
    }
}
