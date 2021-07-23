package com.github.siroshun09.paperupdater.command;

import com.github.siroshun09.paperupdater.papermc.api.ProjectInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.BuildInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.Change;
import com.github.siroshun09.paperupdater.papermc.client.PaperApiClient;
import com.github.siroshun09.paperupdater.util.JarPathFactory;
import com.github.siroshun09.paperupdater.util.Sha256Checker;
import com.github.siroshun09.paperupdater.util.SystemProperties;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;

public class CheckUpdate {

    public static void run() {
        System.out.println();
        System.out.println("Checking updates...");

        var projectName = SystemProperties.getProjectName();
        var projectVersion = SystemProperties.getProjectVersion();

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
