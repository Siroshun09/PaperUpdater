package com.github.siroshun09.paperupdater.util;

import org.jetbrains.annotations.NotNull;

public final class SystemProperties {

    public static @NotNull String getProjectName() {
        var property = System.getProperty("paperupdater.project-name");

        if (property == null || property.isEmpty()) {
            SystemLogger.printError("project-name must not be empty.");
            SystemLogger.printError("Please specify project-name: -Dpaperupdater.project-name=NAME (paper, waterfall)");
            System.exit(1);
            throw new InternalError();
        }

        return property;
    }

    public static @NotNull String getProjectVersion() {
        var property = System.getProperty("paperupdater.project-version");

        if (property == null || property.isEmpty()) {
            SystemLogger.printError("project-version must not be empty.");
            SystemLogger.printError("Please specify project-version: -Dpaperupdater.project-version=VERSION (ex 1.17.1)");
            System.exit(1);
            throw new InternalError();
        }

        return property;
    }

    public static @NotNull String getJarFileName() {
        var property = System.getProperty("paperupdater.jar-file-name", "{project-name}-{project-version}.jar");

        if (property == null || property.isEmpty()) {
            SystemLogger.printError("jar-file-name must not be empty.");
            System.err.println(
                    "Please specify project-name: -Dpaperupdater.jar-file-name=\"{project-name}-{project-version}.jar\""
            );
            SystemLogger.printError("Available placeholders: {project-name}, {project-version}, {project-build-number}");
            System.exit(1);
            throw new InternalError();
        }

        return property;
    }

    public static boolean isLogging() {
        return !Boolean.getBoolean("paperupdater.no-logging");
    }
}
