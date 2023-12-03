package com.mobilesuit.clientplugin.setting;

public class DefaultGeneralSettings {

    public static final boolean USE_MY_OWN_OPEN_AI_KEY = false;
    public static final String OPEN_AI_KEY = "";
    public static final String NOTION_API_KEY = "";
    public static final String NOTION_DATABASE_ID = "";
    public static final String MARKDOWN_FILE_STORAGE_LOCATION = "";
    public static final boolean RECEIVE_COMMIT_RECOMMENDATION = true;
    public static final int STANDARD_FILE_SIZE_CHANGE = 500;

    public static GeneralSettingsState getDefaultSettings() {
        return GeneralSettingsState.builder()
                .useMyOwnOpenAIKey(USE_MY_OWN_OPEN_AI_KEY)
                .openAIKey(OPEN_AI_KEY)
                .notionAPIKey(NOTION_API_KEY)
                .notionDatabaseID(NOTION_DATABASE_ID)
                .markdownFileStorageLocation(MARKDOWN_FILE_STORAGE_LOCATION)
                .build();
    }
}
