//package com.mobilesuit.clientplugin.listener;
//
//import com.intellij.openapi.vcs.ProjectLevelVcsManager;
//import com.intellij.openapi.vcs.changes.*;
//
//import java.util.Collection;
//import java.util.List;
//
//public class MyChangeListListener implements ChangeListListener {
//
//
//    @Override
//    public void changesRemoved(Collection<? extends Change> changes, ChangeList fromList) {
//        System.out.println("changesRemoved");
//        changes.forEach((chages)->{
//            System.out.println(chages.getVirtualFile().getName());
//        });
//
//        fromList.getChanges().forEach((what)->{
//            System.out.println(what.getVirtualFile().getName());
//        });
//    }
//}
//
