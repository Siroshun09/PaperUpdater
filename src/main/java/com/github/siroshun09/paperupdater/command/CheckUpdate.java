package com.github.siroshun09.paperupdater.command;

import com.github.siroshun09.paperupdater.papermc.api.ProjectInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.BuildInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.Change;
import com.github.siroshun09.paperupdater.papermc.client.PaperApiClient;
import com.github.siroshun09.paperupdater.util.JarCache;
import com.github.siroshun09.paperupdater.util.JarPathFactory;
import com.github.siroshun09.paperupdater.util.Sha256Checker;
import com.github.siroshun09.paperupdater.util.SystemLogger;
import com.github.siroshun09.paperupdater.util.SystemProperties;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;

public class CheckUpdate {

    public static void run() {
        SystemLogger.printNewLine();
        SystemLogger.print("Checking updates...");

        var projectName = SystemProperties.getProjectName();
        var projectVersion = SystemProperties.getProjectVersion();

        SystemLogger.printNewLine();
        SystemLogger.print("Project name: " + projectName);
        SystemLogger.print("Project version: " + projectVersion);

        var client = PaperApiClient.create(HttpClient.newHttpClient());

        var projectInfo = getProjectInformation(client, projectName, projectVersion);

        var buildInfo = getBuildInformation(client, projectInfo);

        SystemLogger.print("Latest build: " + buildInfo.getBuild());
        SystemLogger.print("Build time: " + buildInfo.getTime());

        var hash = buildInfo.getDownloads().getApplication().getSha256();

        var jarPath = JarPathFactory.create(projectInfo);

        if (Files.exists(jarPath)) {
            if (Sha256Checker.isSameHash(jarPath, hash)) {
                SystemLogger.printNewLine();
                SystemLogger.print("No updates!");
                return;
            } else {
                try {
                    Files.delete(jarPath);
                } catch (IOException e) {
                    SystemLogger.printErrorAndExit("Could not delete the old jar file", e);
                    return;
                }
            }
        }

        SystemLogger.printNewLine();
        SystemLogger.print("New build is now available!");
        SystemLogger.print("Downloading new build...");
        SystemLogger.printNewLine();

        SystemLogger.print("-------- Changes --------");
        buildInfo.getChanges().stream().map(Change::getMessage).forEach(SystemLogger::print);
        SystemLogger.print("-------------------------");

        var cacheDirectory = SystemProperties.getCacheDirectory();
        var jarCache = cacheDirectory != null ? new JarCache(cacheDirectory, projectInfo, buildInfo.getBuild()) : null;

        if (jarCache != null && jarCache.hasCachedJar()) {
            if (tryToCopyCachedJar(jarCache, hash, jarPath)) {
                SystemLogger.print("Build " + buildInfo.getBuild() + " was copied from cache (" + jarCache.getPath().toAbsolutePath() +
                        ") to " + jarPath.getFileName().toString() + "!");
                return;
            }
        }

        try {
            client.downloadBuild(buildInfo, jarPath);
        } catch (Exception e) {
            SystemLogger.printErrorAndExit("Could not download jar file", e);
            return;
        }

        if (!Sha256Checker.isSameHash(jarPath, hash)) {
            SystemLogger.printError("The SHA-256 hash value of the downloaded file was not as expected!");
            SystemLogger.printError("Delete the file...");

            try {
                Files.delete(jarPath);
            } catch (IOException e) {
                SystemLogger.printErrorAndExit("Could not delete the invalid jar file", e);
            }
            return;
        }

        SystemLogger.printNewLine();
        SystemLogger.print("Build " + buildInfo.getBuild() + " was downloaded to " + jarPath.getFileName().toString() + "!");

        if (jarCache != null) {
            SystemLogger.printNewLine();
            SystemLogger.print("Creating a new cache...");

            try {
                jarCache.saveNewCache(jarPath);
            } catch (IOException e) {
                SystemLogger.printErrorAndExit("Could not save the new cache", e);
            }

            SystemLogger.print("Deleting old cached jar files...");

            try {
                jarCache.deleteOldCaches();
            } catch (IOException e) {
                SystemLogger.printErrorAndExit("Could not delete old cached jar files", e);
            }

            SystemLogger.print("Done!");
        }
    }

    private static ProjectInformation getProjectInformation(@NotNull PaperApiClient client,
                                                            @NotNull String projectName,
                                                            @NotNull String projectVersion) {
        ProjectInformation projectInfo;

        try {
            projectInfo = client.getProjectInformation(projectName, projectVersion);
        } catch (Exception e) {
            SystemLogger.printErrorAndExit("Could not get project information", e);
            throw new InternalError(e);
        }

        if (projectInfo.getBuilds() == null) {
            SystemLogger.printError("Could not get build information");
            SystemLogger.printError("Did you specify the correct project name?");
            System.exit(1);
            throw new InternalError();
        }

        return projectInfo;
    }

    private static BuildInformation getBuildInformation(@NotNull PaperApiClient client,
                                                        @NotNull ProjectInformation info) {
        try {
            return client.getLatestBuild(info);
        } catch (Exception e) {
            SystemLogger.printErrorAndExit("Could not get build information", e);
            throw new InternalError();
        }
    }

    private static boolean tryToCopyCachedJar(@NotNull JarCache jarCache, @NotNull String hash, @NotNull Path dist) {
        if (jarCache.checkSum(hash)) {
            try {
                jarCache.copyCache(dist);
                return true;
            } catch (IOException e) {
                SystemLogger.printErrorAndExit("Could not copy jar file from cache", e);
            }
        } else {
            try {
                SystemLogger.print("Invalid cached jar was found, delete it...");
                jarCache.deleteCache();
            } catch (IOException e) {
                SystemLogger.printErrorAndExit("Could not delete invalid jar file in cache", e);
            }
        }

        return false;
    }
}
