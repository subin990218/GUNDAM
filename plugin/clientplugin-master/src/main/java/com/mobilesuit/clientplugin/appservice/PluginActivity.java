package com.mobilesuit.clientplugin.appservice;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

final class PluginActivity implements StartupActivity {

    private final PluginService pluginService = ApplicationManager.getApplication().getService(PluginService.class);

    @Override
    public void runActivity(@NotNull Project project) {
        /*if(pluginService.getHasRun().compareAndSet(false,true)) {
            pluginService.runService(project);
        }*/
    }
}
