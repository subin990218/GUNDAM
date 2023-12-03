package com.mobilesuit.clientplugin.appservice;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.JBColor;
import com.mobilesuit.clientplugin.client.SocketClient;
import com.mobilesuit.clientplugin.event.handler.EventHandler;
import com.mobilesuit.clientplugin.form.PluginOption;
import com.mobilesuit.clientplugin.listener.MyFileEditorManagerListener;
import com.mobilesuit.clientplugin.listener.MyProjectManagerListener;
import com.mobilesuit.clientplugin.ping.PingCalc;
import com.mobilesuit.clientplugin.renderer.AlertPanel;
import com.mobilesuit.clientplugin.renderer.LivePanel;
import com.mobilesuit.clientplugin.renderer.PingPanel;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public final class PluginService {


    private final DataContainer dataContainer = DataContainer.getInstance();

    @Getter
    private final AtomicBoolean hasRun = new AtomicBoolean(false);

    public PluginService() {
        System.out.println("PluginService 호출");
    }

    public void runService(Project project, PluginOption pluginOption){
        dataContainer.setProjectPath(project.getBasePath());
        dataContainer.setProject(project);

        JFrame mainFrame = dataContainer.updateMainFrame();

        ProgressManager.getInstance().run(new PluginBackGroundTask(project,pluginOption));

        System.out.println("runService");
    }
}
