package com.mobilesuit.clientplugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

public class WriteActionUtil {

    private WriteActionUtil() {}

    public static void invokeLaterWriteAction(Project project, Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, runnable);
        });
    }
}
