package com.mobilesuit.clientplugin.listener;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.mobilesuit.clientplugin.event.service.FileChangeDetector;
import com.mobilesuit.clientplugin.setting.GeneralSettingsState;
import org.jetbrains.annotations.NotNull;

public class My2FileEditorManagerListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Project project = ProjectUtil.guessProjectForFile(file);
        GeneralSettingsState SettingsState = GeneralSettingsState.getInstance(project);
        if(!SettingsState.isReceiveCommitRecommendation()){
            return;
        }
        FileChangeDetector fileChangeDetector = FileChangeDetector.getInstance();
        fileChangeDetector.addOpenVirtualFile(file); //파일 사이즈를 등록한다.

    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Project project = ProjectUtil.guessProjectForFile(file);
        GeneralSettingsState SettingsState = GeneralSettingsState.getInstance(project);
        if(!SettingsState.isReceiveCommitRecommendation()){
            return;
        }
        FileChangeDetector fileChangeDetector = FileChangeDetector.getInstance();
        fileChangeDetector.checkCommit(file); // 커밋 하지 않은 파일을 닫으면 알림을 준다.

    }
}
