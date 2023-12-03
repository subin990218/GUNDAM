package com.mobilesuit.clientplugin.form;

import lombok.Getter;

import javax.swing.*;

public class MainSettingsComponent {
    private JPanel mainPanel;
    private JTabbedPane mainTabbedPane;

    @Getter
    private final GeneralSettingsComponent generalSettingsComponent;

    @Getter
    private final CommitConventionSettingsComponent commitConventionSettingsComponent;

    public MainSettingsComponent() {
        // mainPanel 과 mainTabbedPane 은 Swing GUI Designer 에 의해서 생성된다
        generalSettingsComponent = new GeneralSettingsComponent();
        commitConventionSettingsComponent = new CommitConventionSettingsComponent();

        mainTabbedPane.addTab("General", generalSettingsComponent.getContentPane());
        mainTabbedPane.addTab("Commit Convention", commitConventionSettingsComponent.getContentPane());
    }

    public JPanel getPanel() {
        return mainPanel;
    }

}
