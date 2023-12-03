package com.mobilesuit.clientplugin.util;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
import com.mobilesuit.clientplugin.renderer.GptResultPanel;
import com.mobilesuit.clientplugin.window.GPTResultWindowFactory;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.Timer;
import java.util.TimerTask;

public class AsyncTimeout {
    public Timer timer = new Timer();
    public void startTimer(String name,ToolWindow toolWindow) {
        // 작업 수행
        CompletableFuture.runAsync(() -> {
            //Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Task executed after 5 minutes...");
                    ResponseRepository repo = ResponseRepository.getInstance();

                    if(repo.getCodeReviewMap().get(name) != null){ // 값이 들어있다면
                        System.out.println("return true");
                    }else{ //값이 없다면 창을 바꾼다.
                        changePage(name,toolWindow);
                    }
                    // 타이머 종료
                    timer.cancel();
                }
            }, 5 * 60 * 1000); // 5분을 밀리초로 변환
        });
    }

    private void changePage(String name,ToolWindow toolWindow) {
        System.out.println("-----------changePage--------\n");
        Content[]contentList = toolWindow.getContentManager().getContents();

        for(int i=0;i< contentList.length;i++){
            if(contentList[i].getTabName().equals(name)){ // 탭의 이름과, 현재 들어온 코드명이 일치할때, 그팬을 그려준다.
                JPanel loadingPanel = new JPanel(new BorderLayout());

                ImageIcon LoadingIcon = new ImageIcon(AsyncTimeout.class.getResource("/icon/timeout.png"));
                LoadingIcon.setImage(LoadingIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
                JLabel jLabel = new JLabel(LoadingIcon);
                jLabel.setToolTipText("응답지연으로 인한 연결끊김");
                loadingPanel.add(jLabel, BorderLayout.CENTER);

                contentList[i].setComponent(loadingPanel);
                return;
            }
        }

    }
}
