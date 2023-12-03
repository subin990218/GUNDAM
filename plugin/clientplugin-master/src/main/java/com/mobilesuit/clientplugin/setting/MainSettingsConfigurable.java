package com.mobilesuit.clientplugin.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.mobilesuit.clientplugin.form.CommitConventionSettingsComponent;
import com.mobilesuit.clientplugin.form.GeneralSettingsComponent;
import com.mobilesuit.clientplugin.form.MainSettingsComponent;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainSettingsConfigurable implements Configurable {

    @Getter
    private final Project project;
    private MainSettingsComponent mainSettingsComponent;
    private GeneralSettingsComponent generalSettingsComponent;
    private CommitConventionSettingsComponent commitConventionSettingsComponent;

    public MainSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "GUNDAM";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainSettingsComponent = new MainSettingsComponent();
        generalSettingsComponent = mainSettingsComponent.getGeneralSettingsComponent();
        commitConventionSettingsComponent = mainSettingsComponent.getCommitConventionSettingsComponent();

        DataContainer.getInstance().setMainSettingsConfigurable(this);

        return mainSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        GeneralSettingsState generalSettings = GeneralSettingsState.getInstance(project);
        CommitConventionSettingsState commitConventionSettings = CommitConventionSettingsState.getInstance(project);

        boolean generalSettingsModified = generalSettingsComponent.getUseMyOwnOpenAICheckBox() != generalSettings.isUseMyOwnOpenAIKey()
                || !generalSettingsComponent.getOpenAIKey().equals(generalSettings.getOpenAIKey())
                || !generalSettingsComponent.getNotionAPIKey().equals(generalSettings.getNotionAPIKey())
                || !generalSettingsComponent.getNotionDatabaseID().equals(generalSettings.getNotionDatabaseID())
                || !generalSettingsComponent.getMarkdownFileStorageLocation().equals(generalSettings.getMarkdownFileStorageLocation())
                || generalSettingsComponent.getReceiveCommitRecommendation() != generalSettings.isReceiveCommitRecommendation()
                || generalSettingsComponent.getStandardFileSizeChange() != generalSettings.getStandardFileSizeChange();

        boolean commitConventionSettingsModified = !commitConventionSettingsComponent.getHeaderConvention().equals(commitConventionSettings.getHeaderConvention())
                || !commitConventionSettingsComponent.getBodyConvention().equals(commitConventionSettings.getBodyConvention())
                || !commitConventionSettingsComponent.getFooterConvention().equals(commitConventionSettings.getFooterConvention())
                || commitConventionSettingsComponent.getAllowEmptyBody() != commitConventionSettings.isAllowEmptyBody()
                || commitConventionSettingsComponent.getAllowEmptyFooter() != commitConventionSettings.isAllowEmptyFooter()
                || !(commitConventionSettingsComponent.getProperties() == null || commitConventionSettingsComponent.getProperties().equals(commitConventionSettings.getProperties()))
                || !(commitConventionSettingsComponent.getPropertyOptions() == null || commitConventionSettingsComponent.getPropertyOptions().equals(commitConventionSettings.getPropertyOptions()));
        return generalSettingsModified || commitConventionSettingsModified;
    }

    @Override
    public void apply() {
        GeneralSettingsState generalSettings = GeneralSettingsState.getInstance(project);
        generalSettings.setUseMyOwnOpenAIKey(generalSettingsComponent.getUseMyOwnOpenAICheckBox());
        generalSettings.setOpenAIKey(generalSettingsComponent.getOpenAIKey());
        generalSettings.setNotionAPIKey(generalSettingsComponent.getNotionAPIKey());
        generalSettings.setNotionDatabaseID(generalSettingsComponent.getNotionDatabaseID());
        generalSettings.setMarkdownFileStorageLocation(generalSettingsComponent.getMarkdownFileStorageLocation());
        generalSettings.setReceiveCommitRecommendation(generalSettingsComponent.getReceiveCommitRecommendation());
        generalSettings.setStandardFileSizeChange(generalSettingsComponent.getStandardFileSizeChange());

        CommitConventionSettingsState commitConventionSettings = CommitConventionSettingsState.getInstance(project);
        commitConventionSettings.setHeaderConvention(commitConventionSettingsComponent.getHeaderConvention());
        commitConventionSettings.setBodyConvention(commitConventionSettingsComponent.getBodyConvention());
        commitConventionSettings.setFooterConvention(commitConventionSettingsComponent.getFooterConvention());
        commitConventionSettings.setAllowEmptyBody(commitConventionSettingsComponent.getAllowEmptyBody());
        commitConventionSettings.setAllowEmptyFooter(commitConventionSettingsComponent.getAllowEmptyFooter());
        commitConventionSettings.setProperties(commitConventionSettingsComponent.getProperties());
        commitConventionSettings.setPropertyOptions(copyPropertyOptions(commitConventionSettingsComponent.getPropertyOptions()));
    }

    @Override
    public void reset() {
        GeneralSettingsState generalSettings = GeneralSettingsState.getInstance(project);
        generalSettingsComponent.setUseMyOwnOpenAICheckBox(generalSettings.isUseMyOwnOpenAIKey());
        generalSettingsComponent.setOpenAIKey(generalSettings.getOpenAIKey());
        generalSettingsComponent.setNotionAPIKey(generalSettings.getNotionAPIKey());
        generalSettingsComponent.setNotionDatabaseIDTextField(generalSettings.getNotionDatabaseID());
        generalSettingsComponent.setMarkdownFileStorageLocationTextField(generalSettings.getMarkdownFileStorageLocation());
        generalSettingsComponent.setReceiveCommitRecommendationCheckBox(generalSettings.isReceiveCommitRecommendation());
        generalSettingsComponent.setStandardFileSizeChangeTextField(generalSettings.getStandardFileSizeChange());

        CommitConventionSettingsState commitConventionSettings = CommitConventionSettingsState.getInstance(project);
        commitConventionSettingsComponent.setHeaderConvention(commitConventionSettings.getHeaderConvention());
        commitConventionSettingsComponent.setBodyConvention(commitConventionSettings.getBodyConvention());
        commitConventionSettingsComponent.setFooterConvention(commitConventionSettings.getFooterConvention());
        commitConventionSettingsComponent.setAllowEmptyBody(commitConventionSettings.isAllowEmptyBody());
        commitConventionSettingsComponent.setAllowEmptyFooter(commitConventionSettings.isAllowEmptyFooter());
        commitConventionSettingsComponent.setProperties(commitConventionSettings.getProperties());
        if (commitConventionSettings.getPropertyOptions() == null) {
            commitConventionSettingsComponent.setPropertyOptions(new ArrayList<>());
        } else {
            commitConventionSettingsComponent.setPropertyOptions(copyPropertyOptions(commitConventionSettings.getPropertyOptions()));
        }
    }

    @Override
    public void disposeUIResources() {
        mainSettingsComponent = null;
    }

    private List<PropertyOption> copyPropertyOptions(List<PropertyOption> list) {
        return new ArrayList<>(list.stream().map(PropertyOption::clone).toList());
    }
}
