package com.github.siroshun09.paperupdater.config;

import com.github.siroshun09.configapi.api.value.ConfigValue;

public final class UpdaterSettings {

    public static final ConfigValue<String> PROJECT_NAME = config -> config.getString("project-name");

    public static final ConfigValue<String> PROJECT_VERSION = config -> config.getString("project-version");

    public static final ConfigValue<String> JAR_FILE_NAME = config -> config.getString("jar-file-name");
}
