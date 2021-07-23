package com.github.siroshun09.paperupdater.config;

import com.github.siroshun09.configapi.yaml.YamlConfiguration;

import java.nio.file.Path;

public final class Configurations {

    public static final YamlConfiguration UPDATER_CONFIG = YamlConfiguration.create(Path.of("updater-config.yml"));
}
