package com.mobilesuit.clientplugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.mobilesuit.clientplugin.event.service.FileChangeDetector;
import com.mobilesuit.clientplugin.setting.GeneralSettingsState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyBulkFileListener implements BulkFileListener{
    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {

        events.forEach((event)->{
            VirtualFile file = event.getFile();


            Project project = ProjectUtil.guessProjectForFile(file);
            GeneralSettingsState SettingsState = GeneralSettingsState.getInstance(project);
            if(!SettingsState.isReceiveCommitRecommendation()){
                return;
            }


            FileChangeDetector fileChangeDetector = FileChangeDetector.getInstance();
            fileChangeDetector.checkFileSize(file); //파일 사이즈를 등록한다.
        });
    }
}
