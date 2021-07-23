package com.github.siroshun09.paperupdater.util;

import com.github.siroshun09.paperupdater.papermc.api.ProjectInformation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class JarPathFactory {

    public static Path create(@NotNull ProjectInformation projectInfo) {
        return create(projectInfo, projectInfo.getLatestBuild());
    }

    public static Path create(@NotNull ProjectInformation projectInfo, int buildNum) {
        var result =
                SystemProperties.getJarFileName()
                        .replace("{project-name}", projectInfo.getProjectId())
                        .replace("{project-version}", projectInfo.getVersion())
                        .replace("{project-build-number}", String.valueOf(buildNum));

        if (result.isEmpty()) {
            System.err.println("jar-file-name must not be empty in updater-config.yml");
            System.exit(1);
        }

        return Path.of(result);
    }
}
