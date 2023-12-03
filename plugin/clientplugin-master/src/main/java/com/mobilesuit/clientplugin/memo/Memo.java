package com.mobilesuit.clientplugin.memo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class Memo extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MemoBox memoBox = new MemoBox(e);
    }
}