package com.mobilesuit.clientplugin.gpt.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.mobilesuit.clientplugin.gpt.action.CodeReviewChangeAction;
import com.mobilesuit.clientplugin.gpt.client.ClientRequest;
import com.mobilesuit.clientplugin.gpt.dto.CompletionChatResponse;
import com.mobilesuit.clientplugin.gpt.dto.TokenRequest;
import com.mobilesuit.clientplugin.gpt.repository.CodeInfo;
import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
import com.mobilesuit.clientplugin.gpt.repository.UncommitFile;
import com.mobilesuit.clientplugin.setting.GeneralSettingsState;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.util.MyRestClient;
import com.mobilesuit.clientplugin.util.SecureUtil;
import com.mobilesuit.clientplugin.window.GPTResultWindowFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CodeReviewSideBar extends JPanel {
    private Tree tree;
    // tset
    private String selectedModel;

    private Project p;
    public CodeReviewSideBar(Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi, Project project){
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        //Box.createVerticalGlue();
        this.setLayout(boxLayout);

        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 1. UI 구성하기
        DefaultTreeModel treeModel = TreeModelParsingRender(classToMethodsMapPsi);

        ImageIcon loadingIcon = new ImageIcon(getClass().getResource("/icon/Spinner-1s-200px.gif"));
        loadingIcon.setImage(loadingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

        String[] items = {"gpt-3.5-turbo(추천)", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "lineCommentStyle","async"};

        // JComboBox 생성 및 항목 추가
        ComboBox<String> comboBox = new ComboBox<>(items);
        selectedModel = "gpt-3.5-turbo";
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedModel = (String) comboBox.getSelectedItem();
                    if(selectedModel.equals("gpt-3.5-turbo(추천)")){
                        selectedModel = "gpt-3.5-turbo";
                    }
                    System.out.println("Selected: " + selectedModel);
                }
            }
        });
        //comboBox.setPreferredSize(new Dimension(150, 40));

        JButton extractButton = new JButton("Code Review Request");
        extractButton.addActionListener(event -> {
            extractButton.setIcon(loadingIcon);
            extractButton.setText("Processing");

            // 이전 코드 리뷰 정보 삭제
            ResponseRepository repo = ResponseRepository.getInstance();
            repo.cleanMap();

            //checkedFileSourceCodeList 의 내용을 spring에 전달
            Gson gson = new Gson();

            Map<String,String> checkedSourceCodeMap = getCheckedSourceCode2(project);
            // checkedSourceCodeList가 비어있는지 확인
            ClientRequest clientRequest = new ClientRequest();
            //clientRequest.requestMessage(checkedSourceCodeMap,project,extractButton);

            //String url = "https://k9e207.p.ssafy.io/api/chatgpt/rest/completion/chat/prompt/line/comment";

            if(selectedModel.equals("lineCommentStyle")){
                String url = "https://k9e207.p.ssafy.io/api/chatgpt/rest/completion/chat/prompt/line/comment";
                String user = DataContainer.getInstance().getUserId();; //로그인 안하면 null 이겠지?
                System.out.println("user:" +user);
                //TokenRequest tokenRequest = new TokenRequest("token","user", "");
                clientRequest.requestMessageToken(checkedSourceCodeMap,project,extractButton, user, "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",
                    url,"gpt-3.5-turbo");

            }else if(selectedModel.equals("async")){
                String url = "https://k9e207.p.ssafy.io/api/chatgpt/rest/completion/chat/prompt/async";
                String user = DataContainer.getInstance().getUserId();; //로그인 안하면 null 이겠지?
                if(user ==null){
                    user = "user";
                }
                String token = new String();
                if(GeneralSettingsState.getInstance(p).isUseMyOwnOpenAIKey()){
                    token = GeneralSettingsState.getInstance(p).getOpenAIKey();
                }

                if(token == null){
                    token = "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";
                }
                System.out.println("user: "+user);
                System.out.println("token: "+token);
                //TokenRequest tokenRequest = new TokenRequest("token","user", "");
                clientRequest.requestMessageTokenAsync(checkedSourceCodeMap,project,extractButton, user, token,
                        url,"gpt-3.5-turbo");
            }
            else{
                clientRequest.requestMessage(checkedSourceCodeMap,project,extractButton);
            }
            //if checkbox selected


            SwingUtilities.invokeLater(() -> {
                // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
                String toolWindowId = "CodeReviewSideBar";
                ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow(toolWindowId);

                CodeReviewSiderBarFactory.getInstance().createToolWindowContent2(p,toolWindow);
            });

        }); //button listener end

        p = project;
        JButton renewButton = new JButton("Load");
        renewButton.addActionListener(event -> {
            CodeReviewChangeAction codeReviewChangeAction = new CodeReviewChangeAction();
            List<VirtualFile> uncommitFileList = UncommitFile.getChangeFileList(project);

            // singleTon 클래스에 데이터를 저장하는 로직이 들어가 있어서 함수 호출만 해도 된다
            Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi2 =
                    codeReviewChangeAction.codeClassification(p,uncommitFileList);

            SwingUtilities.invokeLater(() -> {
                // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
                String toolWindowId = "CodeReviewSideBar";
                ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow(toolWindowId);

                CodeReviewSiderBarFactory.getInstance().createToolWindowContent(p,toolWindow);
            });
        });

        flowPanel.add(extractButton);
        flowPanel.add(comboBox);
        flowPanel.add(renewButton);

        flowPanel.setMaximumSize(new Dimension(500, extractButton.getPreferredSize().height+10));
        flowPanel.setPreferredSize(new Dimension(300, extractButton.getPreferredSize().height+10));

        this.add(flowPanel);

        tree = new Tree(treeModel);
        tree.setCellRenderer(new CheckboxTreeCellRenderer());

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForRow(selRow);

                if(selPath != null){
                    Object component = tree.getPathForRow(selRow).getLastPathComponent();
                     CheckboxTreeNode node;

                    //두번 클릭하면 해당 위치로 이동
                    if (e.getClickCount() == 2 && component instanceof CheckboxTreeNode) {
                        node = (CheckboxTreeNode) component;

                        Object userObject = node.getPsiObject();
                        if (userObject instanceof Navigatable) {
                            Navigatable navigatable = (Navigatable) userObject;
                            if (navigatable.canNavigate()) {
                                navigatable.navigate(true);
                            }
                        }
                    }

                    // 우클릭 시 팝업 메뉴 표시
                    if (SwingUtilities.isRightMouseButton(e) && component instanceof CheckboxTreeNode) {
                        final CheckboxTreeNode clickedNode = (CheckboxTreeNode) component;

                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem menuItem = new JMenuItem("MoveTo");
                        popupMenu.add(menuItem);
                        // 필요한 경우, 여기에 추가 메뉴 아이템 추가
                        // menuItem1.addActionListener(...); // 메뉴 아이템에 대한 액션 리스너 추가
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Object userObject = clickedNode.getPsiObject();
                                if (userObject instanceof Navigatable) {
                                    Navigatable navigatable = (Navigatable) userObject;
                                    if (navigatable.canNavigate()) {
                                        navigatable.navigate(true);
                                    }
                                }
                            }
                        });
                        // 팝업 메뉴 표시
                        popupMenu.show(tree, e.getX(), e.getY());
                    }


                    if (selRow != -1 ) {
                        if (component instanceof CheckboxTreeNode) {
                            node = (CheckboxTreeNode) component;
                            boolean currentState = node.isChecked();
                            node.setChecked(!currentState);

                            // 부모 노드가 클릭된 경우 자식 노드들의 상태 변경
                            if (node.getChildCount() > 0) {
                                Enumeration children = node.children();
                                while (children.hasMoreElements()) {
                                    CheckboxTreeNode child = (CheckboxTreeNode) children.nextElement();
                                    child.setChecked(!currentState);
                                }
                            } else {  // 자식 노드가 클릭된 경우 부모 노드의 상태 확인 및 변경
                                CheckboxTreeNode parent = (CheckboxTreeNode) node.getParent();
                                if (parent != null) {
                                    boolean allChildrenChecked = true;
                                    Enumeration siblings = parent.children();
                                    while (siblings.hasMoreElements()) {
                                        CheckboxTreeNode sibling = (CheckboxTreeNode) siblings.nextElement();
                                        if (!sibling.isChecked()) {
                                            allChildrenChecked = false;
                                            break;
                                        }
                                    }
                                    parent.setChecked(allChildrenChecked);
                                }
                            }
                            tree.repaint();
                        } else if (component instanceof DefaultMutableTreeNode) {
                            //아무것도 안하게 하고 싶은데?
                        }
                    }
                }

            }
        });
        JBScrollPane scrollPane = new JBScrollPane(tree);
        this.add(scrollPane);
    }

    //트리 노드 보이는거 만드는 부분
    public DefaultTreeModel TreeModelParsingRender(Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi){
        // 1. UI 구성하기
        DefaultMutableTreeNode root =  new DefaultMutableTreeNode("Project");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        for (Map.Entry<PsiClass, List<PsiMethod>> entry : classToMethodsMapPsi.entrySet()) {
            PsiClass psiClass = entry.getKey();
            CheckboxTreeNode classNode = new CheckboxTreeNode("Class: "+psiClass.getName(),psiClass,"class");
            root.add(classNode);

            for (PsiMethod psiMethod : entry.getValue()) {
                String wholeMethodInfo= new String();
                // 파라미터 정보 얻기
                PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
                wholeMethodInfo ="(";
                for (int i=0;i<parameters.length;i++) {
                    PsiParameter parameter = parameters[i];
                    wholeMethodInfo += parameter.getType().getPresentableText()+" "+parameter.getName();
                    if(i<parameters.length-1)  wholeMethodInfo += ", ";
                }
                wholeMethodInfo += ")";

                CheckboxTreeNode methodNode = new CheckboxTreeNode("Method: "+psiMethod.getName()+wholeMethodInfo, psiMethod, "method");
                classNode.add(methodNode);
            }
        }
        return treeModel;
    }

    //선택된 항목의 소스 코드 반환 모든 트리노드 조회함

    public Map<String,String> getCheckedSourceCode2(Project project) {


        ResponseRepository repo = ResponseRepository.getInstance();
        Map<String,String> checkedNodesInfo = new HashMap<>();

        Enumeration<TreeNode> e = ((DefaultMutableTreeNode)tree.getModel().getRoot()).breadthFirstEnumeration();
        e.nextElement();
        while (((Enumeration<?>) e).hasMoreElements()) {
            CheckboxTreeNode node = (CheckboxTreeNode) e.nextElement();

            if (node.isChecked()) {
                if (node.getPsiObject() instanceof PsiClass) {
                    //클래스라면 하위 항목을 다 포함하고 있다. 하지만 체크되어 있을테니 메소드도 중복으로 보낼것 같다.
                    // 클래스 통째로 코드 보낼일이 있으면 하위 메소드는 리스트에 저장안되도록 해야 한다.
                    PsiClass psiClass = (PsiClass) node.getPsiObject();

                    checkedNodesInfo.put(psiClass.getName(),psiClass.getText());
                    //파일 경로 획득
                    PsiFile file = psiClass.getContainingFile();
                    VirtualFile virtualFile = file.getVirtualFile();
                    String filePath = virtualFile.getPath();

                    //라인 넘버 획득
                    Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                    int lineNumber = document.getLineNumber(psiClass.getTextOffset());
                    // 경주님이 메소드 클래스 이름을 알수 없으니까 리스트로 조회하도록 구현
                    repo.getCodeInfoList().add(new CodeInfo(filePath, lineNumber + 1, "class", psiClass.getName()));
                    repo.getCodeInfoMap().put(psiClass.getName(),new CodeInfo(filePath, lineNumber + 1, "class", psiClass.getName()));

                } else if (node.getPsiObject() instanceof PsiMethod) {
                    PsiMethod psiMethod = (PsiMethod) node.getPsiObject();
                    //부모가 선택되어 있다면 중복 선택 회피
                    CheckboxTreeNode parent = (CheckboxTreeNode) node.getParent();
                    if (parent.isChecked()) continue;

                    //파일 경로 획득
                    PsiFile file = psiMethod.getContainingFile();
                    VirtualFile virtualFile = file.getVirtualFile();
                    String filePath = virtualFile.getPath();

                    PsiCodeBlock body = psiMethod.getBody();
                    if (body != null) {
                        PsiElement firstChild = body.getFirstChild();
                        int lineNumber = -1;
                        if (firstChild != null) {
                            PsiFile containingFile = firstChild.getContainingFile();
                            Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
                            if (document != null) {
                                lineNumber = document.getLineNumber(firstChild.getTextOffset()) + 1;
                                repo.getCodeInfoList().add(new CodeInfo(filePath, lineNumber, "method", psiMethod.getName()));
                                repo.getCodeInfoMap().put(psiMethod.getName(),new CodeInfo(filePath, lineNumber + 1, "method", psiMethod.getName()));
                                // 메소드 정보 저장
                                checkedNodesInfo.put(psiMethod.getName(),psiMethod.getText());

                            }else{
                                System.out.println("project 문제");
                            }
                        }else{
                            System.out.println("psiElement 혹은 body 문제");
                        }
                    }

                }
            }
        }
        return checkedNodesInfo;
    }

    public void reDrawing(Project project){

    }

}