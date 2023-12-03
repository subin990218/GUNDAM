package com.mobilesuit.clientplugin.event.service;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.mobilesuit.clientplugin.setting.GeneralSettingsState;
//import com.mobilesuit.clientplugin.gpt.repository.UncommitFile;


import java.util.*;

@Service
public final class FileChangeDetector {

    public static final String NOTIFICATION_GROUP_ID = "com.mobilesuit.clientplugin.alert.commitRecommand";
    private static Map<String, Long> fileLength = new HashMap<>(); // 선택 된적 있는(열려있을 뿐만아니라 보거나,수정한 파일) 파일의 길이를 저장
    private static Map<String, Integer> isNotification = new HashMap<>(); // 특정 파일에 대해 알림을 다시 보내도 되는지 확인하는 목록
    private static int fileChangeSize = 700;
    //private static Map<String,Integer> NoVcsProject = new HashMap<>();

    private static FileChangeDetector instance = new FileChangeDetector();
    public static FileChangeDetector getInstance(){
        return instance;
    }
    private FileChangeDetector(){
    }
    public void addOpenVirtualFile(VirtualFile virtualFile){ // map에 사이즈를 등록

        String filePath = virtualFile.getPath();
        System.out.println("seleced file getPath : " + virtualFile.getPath());
        System.out.println("seleced file getName : " + virtualFile.getName());

        fileLength.put(filePath, virtualFile.getLength()); //사이즈를 등록
    }
    public void checkCommit(VirtualFile virtualFile){

        Project project = ProjectUtil.guessProjectForFile(virtualFile);
        List<VirtualFile>changedFileList =  getChangeFileList(project); //리스트 받아와서 지금 닫히는 파일이 이 리스트 상에 있을때만 알림을 보내준다.
        if(changedFileList.size() == 0){
            System.out.println("null");
            return; //알림을 주지 않는다.
        }
        System.out.println("changedFileList size : "+changedFileList.size());
        changedFileList.forEach((file) ->{
            if(file.getPath().equals(virtualFile.getPath())){
//                System.out.println("name : " + file.getPath());
//                System.out.println("vir : " + virtualFile.getPath());
                fileCloseNotification(project,virtualFile.getPath());
                return;
            }
        });
        fileLength.remove(virtualFile.getPath());
    }
    public void checkFileSize(VirtualFile file){

        //파일을 연후 일정량 이상의 변경이 생기면 알림을 준다.
        //중간에 커밋이 발생하면 ...여튼 맵의 용량을 기준으로 알림을 준다.
        String filePath = file.getPath();
        if(fileLength.get(file.getPath()) == null) {
            return;
        }
        Project project = ProjectUtil.guessProjectForFile(file);
        if (fileLength.get(filePath) + fileChangeSize <= file.getLength() || fileLength.get(filePath) - fileChangeSize >= file.getLength()) {
            System.out.println(fileLength.get(filePath) + " " + file.getLength());
            showNotification(project, file.getLength(), filePath);

        }
    }
    public List<VirtualFile> getChangeFileList( Project project){ //커밋되어야할 파일리스트 반환
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
            System.out.println(project.getBasePath());
            vcsNotification(project);
            //알림을 보낸후 yes하면 등록하는 페이지로 연결
            //no more message하면 해당 프로젝트에 대해 더이상 알림을 주지 않는다.
        }
        return virtualFileList;
    }
    public static void gitCommit(AnActionEvent e){ // 사용자가 커밋할수있도록한다.
        Project project = e.getProject();
        if (project != null) {
            e.getActionManager().getAction("Git.Commit.Stage").actionPerformed(e);
        }
    }
    public static void vcsConnect(AnActionEvent e){
        Project project = e.getProject();
        if (project != null ){//&& NoVcsProject.get(project.getBasePath())==null) {
            e.getActionManager().getAction("Vcs.QuickListPopupAction").actionPerformed(e);
        }
    }
    public static void fileCloseNotification(Project project,String filePath) {
        String title = "Commit Recommandation";
        String content = "커밋 하시겠습니까 ?\n" + filePath;
        Notification notification = new Notification(FileChangeDetector.NOTIFICATION_GROUP_ID,title, content, NotificationType.INFORMATION);

        // "Yes" action
        notification.addAction(new NotificationAction("Commit") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                gitCommit(e); // commit
                notification.expire();
            }
        });
        // "No" action
        notification.addAction(new NotificationAction("Next") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                notification.expire();
            }
        });
        Notifications.Bus.notify(notification, project);
    }
    public static void showNotification(Project project, long size,String filePath) {

        String title = "Commit Recommandation";
        String content = "커밋 하시겠습니까 ?\n" + filePath;

        Notification notification = new Notification(FileChangeDetector.NOTIFICATION_GROUP_ID, title, content, NotificationType.INFORMATION);

        // "Yes" action
        notification.addAction(new NotificationAction("Commit") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                gitCommit(e); // commit
                notification.expire();
            }
        });
        // "No" action
        notification.addAction(new NotificationAction("Next") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                notification.expire();
            }
        });
        fileLength.replace(filePath, size); // 일단 업데이트해, 알림창을 무시했어도 계속알림을 받을수있도록
        Notifications.Bus.notify(notification, project);
    }
    public static void vcsNotification(Project project) {

        String title = "Vcs Setting";
        String content = "Vcs가 설정 되어있지 않습니다. \n 설정하시겠습니까?";

        Notification notification = new Notification(FileChangeDetector.NOTIFICATION_GROUP_ID, title, content, NotificationType.INFORMATION);
        // "Yes" action
        notification.addAction(new NotificationAction("Yes") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                vcsConnect(e);
                notification.expire();
            }
        });
        // "No" action
        notification.addAction(new NotificationAction("Next") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                //NoVcsProject.put(e.getProject().getBasePath(),1);
                notification.expire();
            }
        });
        Notifications.Bus.notify(notification, project);
    }


    public void printMap(){
        System.out.println("print Map : ");
        fileLength.forEach((filePath, length) -> {
            System.out.println(filePath + " : " + length);
        });
        System.out.println();
    }
}
