package com.github.siroshun09.paperupdater;

import com.github.siroshun09.paperupdater.command.CheckUpdate;

public final class PaperUpdater {

    public static void main(String[] args) {
        if (0 < args.length) {
            if (args[0].equalsIgnoreCase("checkUpdate")) {
                CheckUpdate.run();
            } else {
                System.out.println("Available Commands: help, checkUpdate");
            }
        } else {
            System.out.println("Available Commands: help, checkUpdate");
        }
    }
}
