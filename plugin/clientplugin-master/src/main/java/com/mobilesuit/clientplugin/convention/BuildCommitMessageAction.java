package com.mobilesuit.clientplugin.convention;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.mobilesuit.clientplugin.form.CommitMessageDialog;
import com.mobilesuit.clientplugin.singleton.DataContainer;

import javax.swing.*;
import java.awt.*;

public class BuildCommitMessageAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        CheckinProjectPanel checkinProjectPanel = (CheckinProjectPanel) e.getData(CheckinProjectPanel.PANEL_KEY);

        CommitMessageDialog dialog = new CommitMessageDialog(project, checkinProjectPanel);
        dialog.pack();

        JFrame ideFrame = DataContainer.getInstance().getMainFrame();
        if (ideFrame != null) {
            Dimension ideFrameSize = ideFrame.getSize();
            Dimension dialogSize = dialog.getSize();

            int x = ideFrame.getX() + (ideFrameSize.width - dialogSize.width) / 2;
            int y = ideFrame.getY() + (ideFrameSize.height - dialogSize.height) / 2;

            dialog.setLocation(x, y);
        }

        SwingUtilities.invokeLater(() -> {
            dialog.setVisible(true);
        });
    }
}
