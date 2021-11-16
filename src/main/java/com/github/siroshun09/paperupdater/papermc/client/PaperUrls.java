package com.github.siroshun09.paperupdater.papermc.client;

final class PaperUrls {

    public static final String PROJECT_INFORMATION;

    public static final String BUILD_INFORMATION;

    public static final String DOWNLOAD_ARTIFACT;

    public static final String PROJECT_NAME_PLACEHOLDER;

    public static final String VERSION_PLACEHOLDER;

    public static final String BUILD_NUMBER_PLACEHOLDER;

    public static final String FILE_NAME_PLACEHOLDER;

    static {
        var prefix = "https://papermc.io/api";
        PROJECT_INFORMATION = prefix + "/v2/projects/{project}/versions/{version}";
        BUILD_INFORMATION = prefix + "/v2/projects/{project}/versions/{version}/builds/{build}";
        DOWNLOAD_ARTIFACT = prefix + "/v2/projects/{project}/versions/{version}/builds/{build}/downloads/{download}";

        PROJECT_NAME_PLACEHOLDER = "{project}";
        VERSION_PLACEHOLDER = "{version}";
        BUILD_NUMBER_PLACEHOLDER = "{build}";
        FILE_NAME_PLACEHOLDER = "{download}";
    }
}
