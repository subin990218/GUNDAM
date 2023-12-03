package com.mobilesuit.clientplugin.googlesearch;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class GoogleSearch extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        AnAction copyAction = ActionManager.getInstance().getAction("ProblemsView.CopyProblemDescription");
        copyAction.actionPerformed(e);

        Project project = e.getData(CommonDataKeys.PROJECT);

        if (project != null) {
            String copiedText = getClipboardContents();

            if (copiedText != null && !copiedText.isEmpty()) {
                String searchQuery = "https://www.google.com/search?q=" + copiedText;
                BrowserUtil.browse(searchQuery);
            }
        }
    }

    private String getClipboardContents() {
        Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}