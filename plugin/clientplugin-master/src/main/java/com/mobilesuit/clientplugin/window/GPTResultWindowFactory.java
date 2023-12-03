package com.mobilesuit.clientplugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
import com.mobilesuit.clientplugin.renderer.GptResultPanel;
import com.mobilesuit.clientplugin.util.AsyncTimeout;
import com.mobilesuit.clientplugin.util.ResponseTime;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPTResultWindowFactory implements ToolWindowFactory {
    private static GPTResultWindowFactory instance = new GPTResultWindowFactory();
    private GPTResultWindowFactory(){};
    public static GPTResultWindowFactory getInstance(){
        return instance;
    }
    public static Map<String,Integer> tabId = new HashMap<>();
    //public static Map<String,ResponseTime> timeList = new HashMap<>();

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {

        if(toolWindow == null){
            System.out.println("ToolWindowManager를 찾을 수 없음");
            return;
        }

        ResponseRepository repository = ResponseRepository.getInstance();
        int responseSize = repository.getCodeInfoMap().size();

        System.out.println("----------------createToolWindowContent----------------");

        if(responseSize ==0){
            JLabel loadingLabel = new JLabel("응답이 없습니다.");
            loadingLabel.setForeground(Color.GRAY);
            JPanel contentPanel = new JPanel();
            contentPanel.add(loadingLabel);

            toolWindow.getContentManager().removeAllContents(true);
            toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(contentPanel,"",false));
            // 응답이 없습니다.
            return;
        }


        //map, tabId으로 수정 필요
        toolWindow.getContentManager().removeAllContents(true);
        repository.getCodeInfoList().forEach((codeInfo)->{
            GptResultPanel gptResultPanel = new GptResultPanel(codeInfo.getName());
            SwingUtilities.invokeLater(() -> {
                gptResultPanel.repaintGPTResult(codeInfo.getName());
            });
            toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(gptResultPanel,codeInfo.getName(),false));

        });

//        GptResultPanel gptResultPanel = new GptResultPanel("codeName");
//        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(gptResultPanel,"test",false));
//        toolWindow.show();



    }
    //여기부터
    public void initLoadingToolWindowContent(ToolWindow toolWindow) {
        ResponseRepository repository = ResponseRepository.getInstance();

        if (toolWindow == null) {
            System.out.println("ToolWindowManager를 찾을 수 없음");
            return;
        }
        System.out.println("----------------initLoadingToolWindowContent----------------");


        toolWindow.getContentManager().removeAllContents(true);
        repository.getCodeInfoList().forEach((codeInfo) -> {
            //로딩설정을 이때한다. 팬을 붙인다.
            // 더 적당한 이미지를 크게 붙이기
            JPanel loadingPanel = new JPanel(new BorderLayout());

            ImageIcon LoadingIcon = new ImageIcon(getClass().getResource("/icon/ZKZg.gif"));
            LoadingIcon.setImage(LoadingIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            JLabel jLabel = new JLabel(LoadingIcon);
            jLabel.setToolTipText("각 요청에 대해 최대 5분의 시간이 소요됩니다.");
            loadingPanel.add(jLabel, BorderLayout.CENTER);

            Content content = ContentFactory.getInstance().createContent(loadingPanel, codeInfo.getName(), false);
            //content.setIcon(LoadingIcon);
            toolWindow.getContentManager().addContent(content);

            //ResponseTime responseTime = new ResponseTime();
            //responseTime.setRequestTime(System.currentTimeMillis());
            //timeList.put(codeInfo.getName(),responseTime);

            AsyncTimeout asyncTimeout = new AsyncTimeout();
            asyncTimeout.startTimer(codeInfo.getName(),toolWindow);
            //toolWindow.show();

        });
    }

    public void loadingToolWindowContent(String codeName, ToolWindow toolWindow) {

        // 하나의 응답이 올때마다 더해진다.탭은 미리 만들어 놓고, 응답이 오면 repaint, 그사이엔 로딩 패널과 아이콘을 보여준다
        if(toolWindow == null){
            System.out.println("ToolWindowManager를 찾을 수 없음2");
            return;
        }
        System.out.println("----------------loadingToolWindowContent----------------");

        System.out.println("ToolWindowManage : " + codeName);

        Content []contentList = toolWindow.getContentManager().getContents();

        //timeList.get(codeName).setRequestTime(System.currentTimeMillis());
        //System.out.println("읽어온 응답시간 ------"+codeName + " : " + timeList.get(codeName).getWaitTime());
        for(int i=0;i< contentList.length;i++){
            if(contentList[i].getTabName().equals(codeName)){ // 탭의 이름과, 현재 들어온 코드명이 일치할때, 그팬을 그려준다.
                GptResultPanel gptResultPanel = new GptResultPanel(codeName);
                gptResultPanel.repaintGPTResult(codeName); //해당 탭만 새로그린다.
                contentList[i].setIcon(null);
                contentList[i].setComponent(gptResultPanel);
                return;
            }
        }

    }

    public void printMap(Map<String,String> map){
        map.forEach((filePath, length) -> {
            System.out.println("key : " + filePath + "\n value : " + length);
        });
        System.out.println();
    }


}