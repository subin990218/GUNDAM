package com.mobilesuit.clientplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mobilesuit.clientplugin.documentation.SendMainServer;
import com.mobilesuit.clientplugin.documentation.dto.Doc;
import com.mobilesuit.clientplugin.renderer.GptResultPanel;
//import com.mobilesuit.clientplugin.gpt.repository.UncommitFile;


public class GPTResultOpenAction extends AnAction {
    // 우클릭으로 GPT결과 화면 호출
    @Override
    public void actionPerformed(AnActionEvent e) { // 열기만하고 repaint하지 않는다.
        String toolWindowId = "GPTResult";
        ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow(toolWindowId);
        GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();
        Result.createToolWindowContent(e.getProject(),toolWindow); //붙이는 작업
    }
}