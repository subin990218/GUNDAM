package com.mobilesuit.clientplugin.handler;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/*
@Slf4j
@RequiredArgsConstructor
public class GitCommitHandler extends CheckinHandler {
    private final CheckinProjectPanel myCheckinPanel;
    private final CommitContext commitContext;


    // 커밋 전에 호출되는 메서드
    @Override
    public ReturnResult beforeCheckin() {
        return ReturnResult.COMMIT;
    }

    // 커밋 성공 시 호출되는 메서드
    @Override
    public void checkinSuccessful() {
        log.info("커밋 성공");

        // 커밋된 파일 목록
        Collection<Change> changes = myCheckinPanel.getSelectedChanges();
        for (Change change : changes) {
            VirtualFile file = change.getVirtualFile();
            if (file != null) {
                log.info("커밋된 파일: " + file.getName());
                log.info("파일 경로: " + file.getPath());
            }
        }
    }


    // 커밋 실패 시 호출되는 메서드
    @Override
    public void checkinFailed(@NotNull List<VcsException> exceptions) {
        log.info("커밋 실패");
        for (VcsException exception : exceptions) {
            log.info(exception.getMessage());
        }
    }
}
*/
