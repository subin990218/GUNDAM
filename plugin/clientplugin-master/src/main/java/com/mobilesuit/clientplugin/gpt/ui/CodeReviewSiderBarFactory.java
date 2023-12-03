package com.mobilesuit.clientplugin.gpt.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import com.intellij.ui.content.ContentFactory;
import com.mobilesuit.clientplugin.gpt.repository.ClassToMethodsMapRepository;
import com.mobilesuit.clientplugin.window.GPTResultWindowFactory;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class CodeReviewSiderBarFactory implements ToolWindowFactory {

    private static CodeReviewSiderBarFactory instance = new CodeReviewSiderBarFactory();

    private CodeReviewSiderBarFactory(){}

    public static CodeReviewSiderBarFactory getInstance(){
        return instance;
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {

        if(toolWindow ==null){
            System.out.println("toolWindow 없는데?");
            return;
        }
        toolWindow.getContentManager().removeAllContents(true);

        CodeReviewSideBar codeViewSiderBar = new CodeReviewSideBar(ClassToMethodsMapRepository.getInstance().getClassToMethodsMap(), project);

        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(codeViewSiderBar,"",false));
        toolWindow.show();
    }

    public void createToolWindowContent2(Project project, ToolWindow toolWindow) {
        ClassToMethodsMapRepository.getInstance().getClassToMethodsMap().clear();

        if(toolWindow ==null){
            System.out.println("toolWindow 없는데?");
            return;
        }
        toolWindow.getContentManager().removeAllContents(true);

        CodeReviewSideBar codeViewSiderBar = new CodeReviewSideBar(ClassToMethodsMapRepository.getInstance().getClassToMethodsMap(), project);

        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(codeViewSiderBar,"",false));
        toolWindow.show();
    }
}
