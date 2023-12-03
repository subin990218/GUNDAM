package com.mobilesuit.clientplugin.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Service(Service.Level.PROJECT)
@State(
        name = "com.mobilesuit.clientplugin.setting.GeneralSettingsState",
        storages = @Storage("gundam-general-settings.xml")
)
public final class GeneralSettingsState implements PersistentStateComponent<GeneralSettingsState> {

    private boolean useMyOwnOpenAIKey = DefaultGeneralSettings.USE_MY_OWN_OPEN_AI_KEY;
    private String openAIKey = DefaultGeneralSettings.OPEN_AI_KEY;
    private String notionAPIKey = DefaultGeneralSettings.NOTION_API_KEY;
    private String notionDatabaseID = DefaultGeneralSettings.NOTION_DATABASE_ID;
    private String markdownFileStorageLocation = DefaultGeneralSettings.MARKDOWN_FILE_STORAGE_LOCATION;
    private boolean receiveCommitRecommendation = DefaultGeneralSettings.RECEIVE_COMMIT_RECOMMENDATION;
    private int standardFileSizeChange = DefaultGeneralSettings.STANDARD_FILE_SIZE_CHANGE;

    public static GeneralSettingsState getInstance(Project project) {
        return project.getService(GeneralSettingsState.class);
    }

    @Override
    public @Nullable GeneralSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull GeneralSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void resetToDefaultSettings() {
        GeneralSettingsState defaults = DefaultGeneralSettings.getDefaultSettings();
        XmlSerializerUtil.copyBean(defaults, this);

        MainSettingsConfigurable mainSettingsConfigurable = DataContainer.getInstance().getMainSettingsConfigurable();
        mainSettingsConfigurable.reset();
    }
}
