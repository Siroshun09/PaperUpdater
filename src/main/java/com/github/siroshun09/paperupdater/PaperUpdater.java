package com.github.siroshun09.paperupdater;

import com.github.siroshun09.paperupdater.command.CheckUpdate;
import com.github.siroshun09.paperupdater.util.SystemLogger;

public final class PaperUpdater {

    public static void main(String[] args) {
        if (0 < args.length) {
            if (args[0].equalsIgnoreCase("checkUpdate")) {
                CheckUpdate.run();
            } else {
                SystemLogger.print("Available Commands: help, checkUpdate");
            }
        } else {
            SystemLogger.print("Available Commands: help, checkUpdate");
        }
    }
}
