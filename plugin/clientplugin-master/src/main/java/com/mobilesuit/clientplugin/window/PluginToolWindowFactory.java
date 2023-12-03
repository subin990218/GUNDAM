package com.mobilesuit.clientplugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.mobilesuit.clientplugin.form.PluginOption;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PluginToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SwingUtilities.invokeLater(()->{
            ContentFactory contentFactory = ContentFactory.getInstance();
            Content content = contentFactory.createContent(new PluginOption(project), "", false);
            toolWindow.getContentManager().addContent(content);
        });
    }
}
