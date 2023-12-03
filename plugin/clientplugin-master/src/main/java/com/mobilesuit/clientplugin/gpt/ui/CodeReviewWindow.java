package com.mobilesuit.clientplugin.gpt.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.mobilesuit.clientplugin.gpt.dto.CompletionChatResponse;
import com.mobilesuit.clientplugin.gpt.repository.CodeInfo;
import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;

import com.mobilesuit.clientplugin.util.MyRestClient;
import com.mobilesuit.clientplugin.window.GPTResultWindowFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CodeReviewWindow extends JFrame {
    private Tree tree;
    private JPanel panel;

    public CodeReviewWindow(Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi, Project project){
        setTitle("CodeReview Hierarchy");
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        panel = new CodeReviewSideBar(classToMethodsMapPsi, project);
        add(panel);

        setVisible(true);
    }

    public JPanel getPanel() {
        return panel;
    }

    //트리 노드 보이는거 만드는 부분


    //선택된 항목의 소스 코드 반환 모든 트리노드 조회함
}
class CheckboxTreeNode extends DefaultMutableTreeNode {
    private String type;
    private boolean isChecked;
    private Object psiObject;
    private JButton button;

    public CheckboxTreeNode(String userObject) {
        super(userObject);
        this.isChecked = false;
    }
    public CheckboxTreeNode(String userObject,Object psiObject, String type){
        super(userObject);
        this.psiObject = psiObject;
        this.isChecked = false;
        this.type = type;
    }


    public Object getPsiObject(){
        return psiObject;
    }

    public String getType(){
        return type;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
class CheckboxTreeCellRenderer implements TreeCellRenderer {
    private PsiCheckBox leafRenderer = new PsiCheckBox();
    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof com.mobilesuit.clientplugin.gpt.ui.CheckboxTreeNode) {
            com.mobilesuit.clientplugin.gpt.ui.CheckboxTreeNode checkboxNode = (com.mobilesuit.clientplugin.gpt.ui.CheckboxTreeNode) value;
            String text = checkboxNode.getUserObject().toString();

            // 스타일 적용하기
            if (text.contains("Class:")) {
                text = "<html>" + text.replace("Class:", "<font color='green'>Class:</font>") + "</html>";
            } else if (text.contains("Method:")) {
                text = "<html>" + text.replace("Method:", "<font color='pink'>Method:</font>") + "</html>";
            }

            leafRenderer.setPsiObject(checkboxNode.getPsiObject());
            leafRenderer.setText(text);
            leafRenderer.setSelected(checkboxNode.isChecked());
            return leafRenderer;
        }
        return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}