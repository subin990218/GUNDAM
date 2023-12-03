package com.mobilesuit.clientplugin.listener;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.vfs.VirtualFile;
import com.mobilesuit.clientplugin.appservice.PluginService;
import com.mobilesuit.clientplugin.client.SocketClient;
import com.mobilesuit.clientplugin.event.handler.EventHandler;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.websocket.dto.WebSocketDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Service
@Slf4j
public final class MyFileEditorManagerListener implements FileEditorManagerListener {
    private final SocketClient socketClient = SocketClient.getInstance();
    private final DataContainer dataContainer = DataContainer.getInstance();
    private final EventHandler eventHandler = EventHandler.getInstance();

    @Getter
    @Setter
    private boolean isRegistered = false;

    // 사용자의 탭 목록에 변화가 생겼을 때
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (!isRegistered) {
            openEventHandle(source, file);
            isRegistered = true;
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("닫힌 놈 : " + file.getCanonicalPath());
        eventHandler.panelAlign();
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        changeEventHandle(event);
    }

    private void openEventHandle(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(dataContainer.getProject());
        EditorWindow editorWindow = fileEditorManager.getCurrentWindow();
        editorWindow.getTabbedPane().getComponent().addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        dataContainer.getEditorTimer().restart();
                    }
                });

        System.out.println("열린 놈 : " + file.getCanonicalPath());
    }

    private void changeEventHandle(FileEditorManagerEvent event) {
        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFile virtualFile = event.getNewFile();
            if (null == virtualFile) {
                virtualFile = event.getOldFile();
                System.out.println("지정된 놈이 닫힌게 아닌듯?");
            }
            String currentPath = virtualFile.getCanonicalPath();

            String projectPath = dataContainer.getProjectPath();
            String path = currentPath.replace(projectPath, "");
            dataContainer.setLastFile(path);

            //String pathBackSlash = projectPath.replace("/","\\");

            dataContainer.setProjectPathBackSlash(projectPath);

            Gson gson = new Gson();

            String userName = dataContainer.getUserId();

            String userInfo = gson.toJson(WebSocketDto.UserInfo.builder().code(1).userName(userName).target(path));

            String data = gson.toJson(WebSocketDto.Event.builder().code("USER").text(userInfo));
            socketClient.send(data);

            eventHandler.panelAlign();
        });
    }


}
