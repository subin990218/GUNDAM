package com.mobilesuit.clientplugin.gpt.repository;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.diff.Diff;
import org.apache.batik.gvt.flow.LineInfo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class UncommitFile {
//    private UncommitFile(){}
//    private static UncommitFile instance = new UncommitFile();
//    public UncommitFile getInstance(){return instance;}


// List<VirtualFile> uncommitFile = UncommitFile.getChangeFileList(project); 사용법
public static List<VirtualFile> getChangeFileList(Project project){ //커밋되어야할 파일리스트 반환
        List<VirtualFile> virtualFileList = new ArrayList<>();
        if(project ==null){return virtualFileList;}

        ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project);
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);

        if (vcsManager.hasActiveVcss()) {
        List<LocalChangeList> localChange = changeListManager.getChangeLists();
        LocalChangeList changeList = localChange.get(0);
        for (Change change : changeList.getChanges()) {
        virtualFileList.add(change.getVirtualFile());
        }
        } else {
        System.out.println("사용중인 VCS가 없습니다.");
        //System.out.println("No active VCS configured in the project.");
//            System.out.println(project.getBasePath());
//            vcsNotification(project);
        //알림을 보낸후 yes하면 등록하는 페이지로 연결
        //no more message하면 해당 프로젝트에 대해 더이상 알림을 주지 않는다.
        }
        return virtualFileList;
        }

}
