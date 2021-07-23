package com.github.siroshun09.paperupdater.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class Sha256Checker {

    private static final MessageDigest SHA256;

    static {
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            SystemLogger.printErrorAndExit("Could not get sha-256 algorithm", e);
            throw new InternalError(e);
        }
    }

    public static boolean isSameHash(@NotNull Path path, @NotNull String hash) {
        var fileBytes = readFile(path);
        var hashBytes = fromHex(hash);

        return Arrays.equals(SHA256.digest(fileBytes), hashBytes);
    }

    private static byte[] readFile(@NotNull Path file) {
        try (var input = Files.newInputStream(file)) {
            byte[] buffer = new byte[16 * 1024];
            int off = 0;
            int read;
            while ((read = input.read(buffer, off, buffer.length - off)) != -1) {
                off += read;
                if (off == buffer.length) {
                    buffer = Arrays.copyOf(buffer, buffer.length * 2);
                }
            }
            return Arrays.copyOfRange(buffer, 0, off);
        } catch (final IOException e) {
            SystemLogger.printErrorAndExit("Failed to read all of the data from " + file.toAbsolutePath(), e);
            throw new InternalError(e);
        }
    }

    private static byte[] fromHex(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }

        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            final char left = hex.charAt(i * 2);
            final char right = hex.charAt(i * 2 + 1);
            final byte b = (byte) ((Character.digit(left, 16) << 4) | (Character.digit(right, 16) & 0xF));
            bytes[i] = b;
        }
        return bytes;
    }
}
