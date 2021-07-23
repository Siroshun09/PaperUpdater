package com.github.siroshun09.paperupdater.papermc.client;

import com.github.siroshun09.paperupdater.papermc.api.ProjectInformation;
import com.github.siroshun09.paperupdater.papermc.api.build.BuildInformation;
import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.BUILD_INFORMATION;
import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.BUILD_NUMBER_PLACEHOLDER;
import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.DOWNLOAD_ARTIFACT;
import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.FILE_NAME_PLACEHOLDER;
import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.PROJECT_INFORMATION;
import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.PROJECT_NAME_PLACEHOLDER;
import static com.github.siroshun09.paperupdater.papermc.client.PaperUrls.VERSION_PLACEHOLDER;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@SuppressWarnings("ClassCanBeRecord")
public class PaperApiClient {

    private static final Gson GSON = new Gson();

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull PaperApiClient create(@NotNull HttpClient client) {
        return new PaperApiClient(client);
    }

    private final HttpClient client;

    private PaperApiClient(@NotNull HttpClient client) {
        this.client = client;
    }

    public ProjectInformation getProjectInformation(@NotNull String projectName, @NotNull String version) throws Exception {
        var uri =
                URLBuilder.create(PROJECT_INFORMATION)
                        .replace(PROJECT_NAME_PLACEHOLDER, projectName)
                        .replace(VERSION_PLACEHOLDER, version)
                        .toURI();

        return sendRequestAndParseJson(uri, ProjectInformation.class);
    }

    public BuildInformation getLatestBuild(@NotNull ProjectInformation info) throws Exception {
        var uri =
                URLBuilder.create(BUILD_INFORMATION)
                        .replace(PROJECT_NAME_PLACEHOLDER, info.getProjectId())
                        .replace(VERSION_PLACEHOLDER, info.getVersion())
                        .replace(BUILD_NUMBER_PLACEHOLDER, String.valueOf(info.getLatestBuild()))
                        .toURI();

        return sendRequestAndParseJson(uri, BuildInformation.class);
    }

    public void downloadBuild(@NotNull BuildInformation info, @NotNull Path target) throws Exception {
        var url =
                URLBuilder.create(DOWNLOAD_ARTIFACT)
                        .replace(PROJECT_NAME_PLACEHOLDER, info.getProjectId())
                        .replace(VERSION_PLACEHOLDER, info.getVersion())
                        .replace(BUILD_NUMBER_PLACEHOLDER, String.valueOf(info.getBuild()))
                        .replace(FILE_NAME_PLACEHOLDER, info.getDownloads().getApplication().getName())
                        .toURL();

        try (
                var source = Channels.newChannel(url.openStream());
                var fileChannel = FileChannel.open(target, CREATE, WRITE, TRUNCATE_EXISTING)
        ) {
            fileChannel.transferFrom(source, 0, Long.MAX_VALUE);
        }
    }

    private <T> T sendRequestAndParseJson(@NotNull URI uri, @NotNull Class<T> clazz) throws Exception {
        var request = HttpRequest.newBuilder().uri(uri).header("accept", "application/json").build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return GSON.fromJson(response.body(), clazz);
    }
}
