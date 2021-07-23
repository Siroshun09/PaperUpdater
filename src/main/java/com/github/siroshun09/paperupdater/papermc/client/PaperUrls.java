package com.github.siroshun09.paperupdater.papermc.client;

final class PaperUrls {
    private static final String PREFIX;

    public static final String PROJECT_INFORMATION;

    public static final String BUILD_INFORMATION;

    public static final String DOWNLOAD_ARTIFACT;

    public static final String PROJECT_NAME_PLACEHOLDER;

    public static final String VERSION_PLACEHOLDER;

    public static final String BUILD_NUMBER_PLACEHOLDER;

    public static final String FILE_NAME_PLACEHOLDER;

    static {
        PREFIX = "https://papermc.io/api";
        PROJECT_INFORMATION = PREFIX + "/v2/projects/{project}/versions/{version}";
        BUILD_INFORMATION = PREFIX + "/v2/projects/{project}/versions/{version}/builds/{build}";
        DOWNLOAD_ARTIFACT = PREFIX + "/v2/projects/{project}/versions/{version}/builds/{build}/downloads/{download}";

        PROJECT_NAME_PLACEHOLDER = "{project}";
        VERSION_PLACEHOLDER = "{version}";
        BUILD_NUMBER_PLACEHOLDER = "{build}";
        FILE_NAME_PLACEHOLDER = "{download}";
    }
}
