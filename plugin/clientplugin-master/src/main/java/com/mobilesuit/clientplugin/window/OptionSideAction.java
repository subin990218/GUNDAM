package com.mobilesuit.clientplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

public class OptionSideAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ToolWindow myToolWindow = ToolWindowManager.getInstance(project).getToolWindow("MyToolWindow");
            if (myToolWindow != null) {
                myToolWindow.show(() -> {});
            }
        }
    }
}
