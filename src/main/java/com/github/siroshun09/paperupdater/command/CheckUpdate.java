package com.github.siroshun09.paperupdater.command;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.paperupdater.PaperUpdater;
import com.github.siroshun09.paperupdater.config.Configurations;
import com.github.siroshun09.paperupdater.config.UpdaterSettings;
import com.github.siroshun09.paperupdater.papermc.api.ProjectInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.BuildInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.Change;
import com.github.siroshun09.paperupdater.papermc.client.PaperApiClient;
import com.github.siroshun09.paperupdater.util.JarPathFactory;
import com.github.siroshun09.paperupdater.util.Sha256Checker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;

public class CheckUpdate {

    public static void run() {
        System.out.println();
        System.out.println("Checking updates...");

        if (!Configurations.UPDATER_CONFIG.isLoaded()) {
            loadUpdaterConfig();
        }

        var projectName = getProjectName();
        var projectVersion = getProjectVersion();

        System.out.println();
        System.out.println("Project name: " + projectName);
        System.out.println("Project version: " + projectVersion);

        var client = PaperApiClient.create(HttpClient.newHttpClient());

        var projectInfo = getProjectInformation(client, projectName, projectVersion);

        var buildInfo = getBuildInformation(client, projectInfo);

        System.out.println("Latest build: " + buildInfo.getBuild());
        System.out.println("Build time: " + buildInfo.getTime());

        var hash = buildInfo.getDownloads().getApplication().getSha256();

        var jarPath = JarPathFactory.create(projectInfo);

        if (Files.exists(jarPath)) {
            if (Sha256Checker.isSameHash(jarPath, hash)) {
                System.out.println();
                System.out.println("No updates!");
                return;
            } else {
                try {
                    Files.delete(jarPath);
                } catch (IOException e) {
                    System.err.println("Could not delete the old jar file");
                    e.printStackTrace(System.err);
                    System.exit(1);
                    return;
                }
            }
        }

        System.out.println();
        System.out.println("New build is now available!");
        System.out.println("Downloading new build...");
        System.out.println();

        System.out.println("-------- Changes --------");
        buildInfo.getChanges().stream().map(Change::getMessage).forEach(System.out::println);
        System.out.println("-----------------------");

        try {
            client.downloadBuild(buildInfo, jarPath);
        } catch (Exception e) {
            System.err.println("Could not download jar file");
            e.printStackTrace(System.err);
            System.exit(1);
            return;
        }

        if (!Sha256Checker.isSameHash(jarPath, hash)) {
            System.err.println("The SHA-256 hash value of the downloaded file was not as expected!");
            System.err.println("Delete the file...");

            try {
                Files.delete(jarPath);
            } catch (IOException e) {
                System.err.println("Could not delete the invalid jar file");
                e.printStackTrace(System.err);
            }

            System.exit(1);
            return;
        }

        System.out.println();
        System.out.println("Build " + buildInfo.getBuild() + " was downloaded to " + jarPath.getFileName().toString() + "!");
        System.exit(0);
    }

    private static void loadUpdaterConfig() {
        System.out.println();
        System.out.println("Loading updater-config.yml...");

        var path = Configurations.UPDATER_CONFIG.getPath();

        if (!Files.isRegularFile(path)) {
            try {
                ResourceUtils.copyFromClassLoaderIfNotExists(
                        PaperUpdater.class.getClassLoader(), "updater-config.yml", path
                );
            } catch (IOException e) {
                System.err.println("Could not save default updater-config.yml");
                e.printStackTrace(System.err);
                System.exit(1);
                throw new InternalError(e);
            }
        }

        try {
            Configurations.UPDATER_CONFIG.load();
        } catch (IOException e) {
            System.err.println("Could not load updater-config.yml");
            e.printStackTrace(System.err);
            System.exit(1);
            throw new InternalError(e);
        }
    }

    private static String getProjectName() {
        var projectName = Configurations.UPDATER_CONFIG.get(UpdaterSettings.PROJECT_NAME);

        if (projectName.isEmpty()) {
            System.err.println("project-name must not be empty in updater-config.yml");
            System.exit(1);
            throw new InternalError();
        }

        return projectName;
    }

    private static String getProjectVersion() {
        var projectVersion = Configurations.UPDATER_CONFIG.get(UpdaterSettings.PROJECT_VERSION);

        if (projectVersion.isEmpty()) {
            System.err.println("project-version must not be empty in updater-config.yml");
            System.exit(1);
            throw new InternalError();
        }

        return projectVersion;
    }

    private static ProjectInformation getProjectInformation(@NotNull PaperApiClient client,
                                                            @NotNull String projectName,
                                                            @NotNull String projectVersion) {
        ProjectInformation projectInfo;

        try {
            projectInfo = client.getProjectInformation(projectName, projectVersion);
        } catch (Exception e) {
            System.err.println("Could not get project information");
            e.printStackTrace(System.err);
            System.exit(1);
            throw new InternalError();
        }

        if (projectInfo.getBuilds() == null) {
            System.err.println("Could not get build information");
            System.err.println("Did you specify the correct project name?");
            System.exit(1);
            throw new InternalError();
        }

        return projectInfo;
    }

    private static BuildInformation getBuildInformation(@NotNull PaperApiClient client,
                                                        @NotNull ProjectInformation info) {
        BuildInformation buildInfo = null;

        try {
            buildInfo = client.getLatestBuild(info);
        } catch (Exception e) {
            System.err.println("Could not get build information");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        return buildInfo;
    }
}
