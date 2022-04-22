package com.github.siroshun09.paperupdater.util;

import com.github.siroshun09.paperupdater.papermc.api.ProjectInformation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class JarCache {

    private final Path directory;
    private final ProjectInformation info;
    private final long buildNumber;

    public JarCache(@NotNull Path directory, @NotNull ProjectInformation info, long buildNumber) {
        this.directory = directory;
        this.info = info;
        this.buildNumber = buildNumber;
    }
    
    public @NotNull Path getPath() {
        return directory.resolve(createJarName());
    }

    public boolean hasCachedJar() {
        return Files.isRegularFile(getPath());
    }

    public boolean checkSum(@NotNull String hash) {
        return Sha256Checker.isSameHash(getPath(), hash);
    }

    public void copyCache(@NotNull Path dist) throws IOException {
        copyFile(getPath(), dist);
    }

    public void saveNewCache(@NotNull Path sourcePath) throws IOException {
        copyFile(sourcePath, getPath());
    }

    public void deleteCache() throws IOException {
        Files.deleteIfExists(getPath());
    }

    public void deleteOldCaches() throws IOException {
        try (var stream = Files.list(directory)) {
            stream.forEach(path -> {
                try {
                    checkAndDeleteCache(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void checkAndDeleteCache(@NotNull Path path) throws IOException {
        var filename = path.getFileName().toString();

        if (!filename.equals(createJarName())) {
            Files.deleteIfExists(path);
        }
    }

    private @NotNull String createJarName() {
        return info.getProjectId() + "-" + info.getVersion() + "-" + buildNumber + ".jar";
    }

    private void copyFile(@NotNull Path sourcePath, @NotNull Path dist) throws IOException {
        var parent = dist.toAbsolutePath().getParent();
        if (!Files.isDirectory(parent)) {
            Files.createDirectories(parent);
        }

        try (
                var source = FileChannel.open(sourcePath, StandardOpenOption.READ);
                var fileChannel = FileChannel.open(dist, CREATE, WRITE, TRUNCATE_EXISTING)
        ) {
            fileChannel.transferFrom(source, 0, Long.MAX_VALUE);
        }
    }
}
