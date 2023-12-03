package com.mobilesuit.clientplugin.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.XCollection;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Service(Service.Level.PROJECT)
@State(
        name = "com.mobilesuit.clientplugin.setting.CommitConventionSettingsState",
        storages = @Storage("gundam-commit-convention-settings.xml")
)
public final class CommitConventionSettingsState implements PersistentStateComponent<CommitConventionSettingsState> {

    private List<String> properties = DefaultCommitConventionSettings.PROPERTIES;
    @XCollection(style = XCollection.Style.v2)
    private List<PropertyOption> propertyOptions = DefaultCommitConventionSettings.PROPERTY_OPTIONS;
    private String headerConvention = DefaultCommitConventionSettings.HEADER_CONVENTION;
    private String bodyConvention = DefaultCommitConventionSettings.BODY_CONVENTION;
    private String footerConvention = DefaultCommitConventionSettings.FOOTER_CONVENTION;
    private boolean allowEmptyBody = DefaultCommitConventionSettings.ALLOW_EMPTY_BODY;
    private boolean allowEmptyFooter = DefaultCommitConventionSettings.ALLOW_EMPTY_FOOTER;

    public static CommitConventionSettingsState getInstance(Project project) {
        return project.getService(CommitConventionSettingsState.class);
    }

    @Override
    public @Nullable CommitConventionSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CommitConventionSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void resetToDefaultSettings() {
        CommitConventionSettingsState defaults = DefaultCommitConventionSettings.getDefaultSettings();
        XmlSerializerUtil.copyBean(defaults, this);

        MainSettingsConfigurable mainSettingsConfigurable = DataContainer.getInstance().getMainSettingsConfigurable();
        mainSettingsConfigurable.reset();
    }
}
