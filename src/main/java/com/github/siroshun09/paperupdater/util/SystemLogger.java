package com.github.siroshun09.paperupdater.util;

import org.jetbrains.annotations.NotNull;

public final class SystemLogger {

    private static final boolean LOGGING = SystemProperties.isLogging();

    public static void printNewLine() {
        if (LOGGING) {
            System.out.println();
        }
    }

    public static void print(@NotNull String log) {
        if (LOGGING) {
            System.out.println(log);
        }
    }

    public static void printError(@NotNull String log) {
        System.err.println(log);
    }

    public static void printErrorAndExit(@NotNull String log, @NotNull Throwable e) {
        System.err.println(log);
        e.printStackTrace(System.err);
        System.exit(1);
        throw new InternalError(e);
    }
}
