//package com.mobilesuit.clientplugin.renderer;
//
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.EditorFactory;
//import com.intellij.openapi.editor.event.EditorMouseEvent;
//import com.intellij.openapi.editor.event.EditorMouseEventArea;
//import com.intellij.openapi.editor.event.EditorMouseListener;
//import com.intellij.openapi.fileEditor.FileDocumentManager;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.project.ProjectUtil;
//import com.intellij.openapi.util.TextRange;
//import com.intellij.openapi.vfs.LocalFileSystem;
//import com.intellij.openapi.vfs.VirtualFile;
//
//import com.intellij.icons.AllIcons;
//import com.intellij.psi.*;
//import com.intellij.ui.components.JBScrollPane;
//
//import com.mobilesuit.clientplugin.gpt.repository.CodeInfo;
//import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
//import com.mobilesuit.clientplugin.documentation.CodeToDoc;
//
//import javax.swing.*;
//
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.awt.*;
//
//import java.io.File;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//
//public class CodeViewPanel extends JPanel {
//    private boolean[] subwindow = {false,false,false,false};
//    public Document doc1 = EditorFactory.getInstance().createDocument("//주석이 추가된 코드\n");
//    public Document doc2 = EditorFactory.getInstance().createDocument("//클린 코드\n");
//    public Editor editor1 = EditorFactory.getInstance().createEditor(doc1);
//    public Editor editor2 = EditorFactory.getInstance().createEditor(doc2);
//    public JTextArea codeReview = new JTextArea();
//    public JTextArea commitTitle = new JTextArea();
//
//    //주석이 달린 코드, repaint 실행이후에 사용가능
//    private String commentedCode; // 기존코드와 주석이 합쳐진것
//    private String changedComment; // 주석처리된 주석
//    private String filePath;
//
//    private PsiField[] classPsiField; // 클래스의 멤버변수 저장
//    public int resId; //map으로 변경
//    // 단일 메소드 요청에 대해 PsiMethod 반화느 repain 실행이후 사용가능
//    public PsiMethod oneMethod = null;// 받은 코드가 클래스일땐 사용되지 않는다.
//
//
//// 4분할 화면
//    public CodeViewPanel(String codeName){//(int responseIndex) {
//
//        // 기본 설정 start
//        int marginSize = 10;
//        setLayout(new GridBagLayout()); //전체 창에 대한 설정
//
//        JPanel jPanel = new JPanel(new GridBagLayout()); // 모든 창을 담는 panel
//        jPanel.setBackground(new Color(60, 63, 64));
//        GridBagConstraints gridBagConstraints = new GridBagConstraints(); // jPanel에 대한 정렬 제약사항
//
//        // 세로 2개의 panel
//        JPanel codePanel = new JPanel(new GridBagLayout());
//        codePanel.setBorder(BorderFactory.createEmptyBorder(0, marginSize, marginSize, 5)); //간격
//        JPanel analysisPanel = new JPanel(new GridBagLayout());
//        analysisPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, marginSize, marginSize));
//
//        // 스크롤 배경제거
//        JScrollPane jScrollPane = new JBScrollPane();
//       //jScrollPane.setBorder(null);
//
//        // codePanel,analysisPanel을 여닫는 토글 버튼
//        JToggleButton toggleButton1;
//        JToggleButton toggleButton2;
//
//        toggleButton1 = togglePanel(codePanel);
//        toggleButton2 = togglePanel(analysisPanel);
//
//        // 창닫기 이벤트 등록
//        toggleButton1 = toggleLock(codePanel, toggleButton1, toggleButton2);
//        toggleButton2 = toggleLock(analysisPanel, toggleButton2, toggleButton1);
//
////        gridBagConstraints = settingConst(gridBagConstraints, 0, 0, 0.05, 1, 1, 1,false);
////        jPanel.add(toggleButton1,gridBagConstraints);
////        gridBagConstraints = settingConst(gridBagConstraints, 0, 1, 0.05, 1, 1, 1,false);
////        jPanel.add(toggleButton2,gridBagConstraints);
//        // 기본 설정 end
//
//
//
//        //1.주석이 추가된 코드
//        JPanel title1 = titlePanel("Comment", AllIcons.Actions.EditSource, "주석 적용하기"); //title
//        editor1 = setEditor(editor1,"editor1"); //editro
//        openSubWindow2(editor1, 0, "Comment");// 더블 클릭 이벤트 등록
//
//        //글자 스타일
////        EditorColorsScheme defaultColorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
////        TextAttributesKey reservedWordKey = TextAttributesKey.createTextAttributesKey("KEYWORD");
////        TextAttributes reservedWordAttributes = defaultColorsScheme.getAttributes(reservedWordKey);
////        editor1.getColorsScheme().setAttributes(reservedWordKey,reservedWordAttributes);
//
//        //2.클린코드
//        JPanel title2 = titlePanel("Clean Code", AllIcons.Actions.ForceRefresh, "..."); //title
//        editor2 = setEditor(editor2,"editor2"); //editor
//        openSubWindow2(editor2, 1, "Clean Code");//더블 클릭 이벤트 등록
//
//        //주석적용, 클린코드 버튼 이벤트 등록
//        // 주석을 실제 코드에 붙여놓음
//        title1.getComponent(2).addMouseListener(new MouseAdapter() { // comment window repaint
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                applyComment(codeName); //코드명으로 검색한다.
//            }
//        });
//
//        //웹으로 보내기 버튼 필요
//        title2.getComponent(2).addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
//            }
//        });
//
//        //1,2 등록
//        gridBagConstraints = settingConst(gridBagConstraints, 0, 0, 0.1, 1, 1, 1,true);
//        title1.setBackground(new Color(211, 113, 113));
//        codePanel.add(title1, gridBagConstraints); //주석 title
//
//        gridBagConstraints = settingConst(gridBagConstraints, 1, 0, 0.4, 1, 1, 1,true);
//        codePanel.add(jScrollPane.add(editor1.getComponent()), gridBagConstraints); //주석 editor
//        editor1.getComponent().setBackground(new Color(229, 4, 4));
////        editor1.getComponent().setMinimumSize(new Dimension(350,370));
////        editor1.getComponent().setMaximumSize(new Dimension(600,370));
//        System.out.println("editor1 의 높이 : "+editor1.getComponent().getHeight());
//        //codePanel.add(new JScrollPane(editor1.getComponent()),gridBagConstraints);
//
//        gridBagConstraints = settingConst(gridBagConstraints, 2, 0, 0.1, 1, 1, 1,true);
//        title2.setBackground(new Color(93, 8, 8));
//        codePanel.add(title2, gridBagConstraints); //클린코드 title
//
//        gridBagConstraints = settingConst(gridBagConstraints, 3, 0, 0.4, 1, 5, 1,true);
//        codePanel.add(jScrollPane.add(editor2.getComponent()), gridBagConstraints); //클린코드 editor
//        editor2.getComponent().setBackground(new Color(222, 203, 203));
////        editor2.getComponent().setMinimumSize(new Dimension(350,370));
////        editor2.getComponent().setMaximumSize(new Dimension(600,370));
//        System.out.println("editor2 의 높이 : "+editor2.getComponent().getHeight());
//        // codePanel 등록
//        gridBagConstraints = settingConst(gridBagConstraints, 1, 0, 1, 0.5, 1, 1,true);
//        jPanel.add(codePanel, gridBagConstraints);
//
//
//
//        //3.코드리뷰
//        JPanel title3 = titlePanel("Code Review", AllIcons.Actions.Download, "문서화"); //title
//        codeReview = setJTextArea(codeReview,"codeReview"); //editor
//        openSubWindow1(codeReview, 2, "Code Review"); //더블클릭 이벤트 등록
//
//        //4.커밋 제목
//        JPanel title4 = titlePanel("Commit Message", AllIcons.Actions.ForceRefresh, "커밋에 적용하기"); //title
//        commitTitle = setJTextArea(commitTitle,"commitTitle"); //editor
//        openSubWindow1(commitTitle, 3, "Commit Message"); //더블클릭 이벤트 등록
//
//        //코드리뷰, 커밋적용 버튼 이벤트 등록
//        // 코드 문서화, DB저장
//        title3.getComponent(2).addMouseListener(new MouseAdapter() { // 해당 파일을 문서화한다.
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                //System.out.println("--------------------------------------------");
//                ResponseRepository repo = ResponseRepository.getInstance();
//                //여기부터 다시 수정
//                CodeToDoc codeToDoc = CodeToDoc.getInstance(repo.getCodeInfoMap().get(codeName),commentedCode,repo.getCodeReviewMap().get(codeName)
//                        ,repo.getCleanCodeMap().get(codeName),oneMethod,classPsiField,filePath);
//                codeToDoc.makeMarkDownDoc();
//            }
//        });
//
//        title4.getComponent(2).addMouseListener(new MouseAdapter() { // 커밋적용
//            @Override
//            public void mouseClicked(MouseEvent e) {
//            }
//        });
//
//
//
//        // 3,4 등록
//        gridBagConstraints = settingConst(gridBagConstraints, 0, 0, 0.1, 1, 1, 1,true);
//        analysisPanel.add(title3, gridBagConstraints); // 코드리뷰 title
//        title3.setBackground(new Color(93, 199, 48));
//
//        gridBagConstraints = settingConst(gridBagConstraints, 1, 0, 0.4, 1, 1, 1,true);
//        JPanel codeReviewPanel = new JPanel();
//        codeReviewPanel.setMinimumSize(new Dimension());
//        codeReviewPanel.add(new JScrollPane(codeReview));
//        analysisPanel.add(codeReviewPanel, gridBagConstraints);//코드리뷰 editor
//        //jScrollPane.add(codeReview),
//        codeReview.setBackground(new Color(122, 197, 92));
////        codeReview.setMinimumSize(new Dimension(400,450));
////        codeReview.setMaximumSize(new Dimension(700,450));
//        System.out.println("analysisPanel 사이즈 : "+ analysisPanel.getHeight());
//        System.out.println("codeReview의 높이 : " + codeReview.getHeight());
//
//        gridBagConstraints = settingConst(gridBagConstraints, 2, 0, 0.1, 1, 1, 1,true);
//        analysisPanel.add(title4, gridBagConstraints);// 커밋제목 title
//        title4.setBackground(new Color(17, 54, 2));
//
//        gridBagConstraints = settingConst(gridBagConstraints, 3, 0, 0.4, 1, 1, 1,true);
//        JPanel commitTitlePanel = new JPanel(new BorderLayout());
////        commitTitlePanel.setMinimumSize(new Dimension(400,10));
////        commitTitlePanel.setMaximumSize(new Dimension(700,10));
//        commitTitlePanel.add(new JScrollPane(commitTitle),BorderLayout.CENTER);
//        commitTitlePanel.setBackground(new Color(245, 214, 214));
//        analysisPanel.add(commitTitlePanel, gridBagConstraints);
//        //analysisPanel.add(new JScrollPane(commitTitle), gridBagConstraints);//커밋제목 editor
//        //jScrollPane.add(commitTitle),
//        commitTitle.setBackground(new Color(64, 73, 59));
////        commitTitle.setMinimumSize(new Dimension(400,5));
////        commitTitle.setMaximumSize(new Dimension(700,5));
//        System.out.println("commitTitle 높이 : " + commitTitle.getHeight());
//        System.out.println("commitTitle 최대 : " + commitTitle.getMaximumSize().getHeight());
//        System.out.println("commitTitle 최소 : " + commitTitle.getMinimumSize().getHeight());
//
//        // analysis panel 등록
//        gridBagConstraints = settingConst(gridBagConstraints, 1, 1, 1, 0.5, 1, 1,true);
//        jPanel.add(analysisPanel, gridBagConstraints);
//
//        //jPanel 등록
//        GridBagConstraints Constraints = new GridBagConstraints();
//        Constraints = settingConst(Constraints, 0, 0, 1, 1, 1, 1,true);
//        jPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        add(jPanel, Constraints);
//
//    }
//    private void applyComment(String codeName){
//        System.out.println("applyComment");
//
//        ResponseRepository respo = ResponseRepository.getInstance();
//
//        if (respo.getCodeCommentsMap().get(codeName) == null) {
//            System.out.println("응답 받은 주석 없음");
//            return;
//        }
//
//        StringBuilder updatedDoc1 = new StringBuilder();
//
//        File file = new File(respo.getCodeInfoMap().get(codeName).getFilePath());
//        //File file = new File(codeInsetInfo.get(responseIndex).getFilePath());
//        //File file = new File("D:/E207/gitlab/plugin demo/S09P31E207/src/main/java/com/example/demoplugin/kkj/TestCode/fileChangeAction.java");
//        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
//        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
//        Document code = fileDocumentManager.getDocument(virtualFile);
//
//        //int insertOffset = code.getLineStartOffset(codeInsetInfo.get(responseIndex).getLineNum() - 1);
//        int insertOffset = code.getLineStartOffset(respo.getCodeInfoMap().get(codeName).getLineNum() - 1);
//        //String comment = "changeToComment(codeCommentsList.get(0));\n";
//        String comment = changedComment;
//        //int insertOffset = code.getLineStartOffset(0); // 0번부터 시작
//        updatedDoc1.append(code.getText(new TextRange(0, insertOffset)));
//        updatedDoc1.append("\n");
//        updatedDoc1.append(comment);
//        updatedDoc1.append(code.getText(new TextRange(insertOffset, code.getTextLength())));
//
//
//
//        ApplicationManager.getApplication().runWriteAction(() -> {
//            try {
//                OutputStream outputStream = virtualFile.getOutputStream(this); // 'this' could be your plugin component.
//                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
//                writer.write(updatedDoc1.toString());
//                writer.close();
//                outputStream.close();
//            } catch (Exception ioe) {
//                ioe.printStackTrace();
//            }
//        });
//    }
//    public void repaintGPTResult(String codeName) {
//        System.out.println("repaintGPTResult");
//        ResponseRepository repo = ResponseRepository.getInstance();
//
//        // null 처리필요?
//        CodeInfo repoCodeInfo = repo.getCodeInfoMap().get(codeName);
//        String repoComment = repo.getCodeCommentsMap().get(codeName);
//        String repoCleanCode = repo.getCleanCodeMap().get(codeName);
//        String repoCodeReview = repo.getCodeReviewMap().get(codeName);
//        String repoCommitMessage = repo.getCommitMessageMap().get(codeName);
//
//        StringBuilder updatedDoc;
//        String comment = changeToComment(repoComment);
//        changedComment = comment;
//        //String comment = changeToComment("codeCommentsList.get(0)");
//
//        File file = new File(repoCodeInfo.getFilePath());
//        //File file = new File("D:/E207/gitlab/plugin demo/S09P31E207/src/main/java/com/example/demoplugin/kkj/TestCode/gitInfoAction.java");
//        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
//        filePath = virtualFile.getPath();
//        if (virtualFile == null) {
//            System.err.println("VirtualFile is null. File not found.");
//            return;
//        }
//        // comment repaint
//        System.out.println(repoCodeInfo.getName()+" type: "+repoCodeInfo.getType());
//
//        if (repoCodeInfo.getType().equals("class")) { // class의 경우 - 클래스 앞에 주석 작성
//            try {
//                //여기부터
//                FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
//                Document code = fileDocumentManager.getDocument(virtualFile);
//                updatedDoc = new StringBuilder();
//
//
//                if (code != null) {
//                    //System.out.println("repaint");
//                    //클래스의 멤버 변수를 읽어온다.
//                    Project project = ProjectUtil.guessProjectForFile(virtualFile);
//                    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
//                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
//
//                    PsiClass[] psiClasses = javaFile.getClasses();
//                    for (PsiClass psiClass : psiClasses) {
//                        if (psiClass != null) {
//                            // PsiField 배열을 사용하여 모든 멤버 변수 가져오기
//                            classPsiField = psiClass.getFields();
//                        }
//                    }
//                    //클래스의 멤버변수를 읽어온다.
//
//                    int insertOffset = code.getLineStartOffset(repoCodeInfo.getLineNum() - 1);
//                    //updatedDoc.append(code.getText(new TextRange(0, insertOffset)));
//                    updatedDoc.append("\n");
//                    updatedDoc.append(comment); // 주석 삽입
//                    //updatedDoc1.append("comment"); // 주석 삽입
//
//                    updatedDoc.append(code.getText(new TextRange(insertOffset, code.getTextLength())));
//
//                    commentedCode = updatedDoc.toString();
//                    //System.out.println("commentedCode : " + commentedCode); // 왜 얘가 null 인지
//                    ApplicationManager.getApplication().runWriteAction(() -> {
//                        editor1.getDocument().setText(updatedDoc.toString());
//                        editor1.getComponent().repaint();
//                    });
////                        ApplicationManager.getApplication().runWriteAction(() -> { // 이방법도 가능
////                            doc1.setText(updatedDoc);
////                        });
////                        editor1 = EditorFactory.getInstance().createEditor(doc1);
////                        editor1.getComponent().repaint();
//
//
//
//                } else {
//                    System.err.println("Document is null. Cannot read file content.");
//                }
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//        } else { //메서드의 경우 - 해당 메서드만 읽어와서 그앞에 주석작성
//            try {
//                String methodName = repoCodeInfo.getName();
//                //String methodName = "actionPerformed";
//
//                Project project = ProjectUtil.guessProjectForFile(virtualFile);
//                PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
//                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
//
//                PsiMethod methodCode = null;
//                PsiClass[] classes = javaFile.getClasses();
//                for (PsiClass psiClass : classes) {
//                    PsiMethod[] methods = psiClass.getMethods();
//                    for (PsiMethod method : methods) {
//                        //System.out.println("find Name : "+ method.getName());
//                        if (method.getName().equals(methodName)) {
//                            oneMethod = method;
//
//                            //메서스 자체를 넣으려면, 아래처럼
////                                    int startOffset = method.getTextOffset();
////                                    int endOffset = startOffset+ method.getTextLength();
//                            PsiCodeBlock body = method.getBody();
//                            if (body != null) {
//                                //PsiStatement[] statements = body.getStatements();
//                                methodCode = method;
//                                break;
//                            }
//                        }
//                    }
//                }
//                updatedDoc = new StringBuilder();
//                updatedDoc.append(comment);
//                updatedDoc.append("\n");
//                updatedDoc.append(methodCode.getText());
//                commentedCode = updatedDoc.toString();
//
//                ApplicationManager.getApplication().runWriteAction(() -> {
//                    editor1.getDocument().setText(updatedDoc.toString());
//                    editor1.getComponent().repaint();
//                });
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//        }// comment repaint
//
//        //clean code repaint - 값이 없을 수도 있다.
//
//        ApplicationManager.getApplication().runWriteAction(() -> {
//            if(repoCleanCode != null){
//                editor2.getDocument().setText(repoCleanCode);
//            }else{
//                editor2.getDocument().setText("해당 코드에 대해선 클린코드를 제공하지 않습니다.");
//            }
//            editor2.getComponent().repaint();
//        });
//        //clean code repaint
//
//        //codeReview repaint
//        codeReview.setText(repoCodeReview); // 이 처리가 필요한지 확인
//        //codeReview.setText("testtest");
//        codeReview.repaint();
//        //codeReview repaint
//
//        //commit message repaint
//        commitTitle.setText(repoCommitMessage);
//        //codeReview.setText("testtest");
//        commitTitle.repaint();
//        //commit message repaint
//
//
//
//    }
//    private String changeToComment(String str){ // 주석 형태로 처리
//
//        if(str == null){
//            return "주석이 없습니다.";
//        }
//        String []comment = str.split("\n");
//        StringBuilder result = new StringBuilder();
//        for(int i=0;i<comment.length;i++){
//            String res;
//            if(comment[i].startsWith("[")){
//                res = "//" + comment[i].substring(1,comment[i].length()) + "\n";
//            }else if(comment[i].endsWith("]")){
//                res = "//" + comment[i].substring(0,comment[i].length()-1) + "\n";
//            }else{
//                res = "//" + comment[i] + "\n";
//            }
//            result.append(res);
//        }
//        return result.toString();
//    }
//    public void openSubWindow1(JTextArea panel,int Id,String title){
//        //System.out.println(Id + " : " + subwindow[Id]);
//
//        panel.addMouseListener(new MouseAdapter() {
//            private int clickCount = 0;
//            private long lastClickTime = 0;
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                long currentTime = System.currentTimeMillis();
//
//                if (clickCount == 1 && currentTime - lastClickTime <= 500 && !subwindow[Id]) {
//                    //System.out.println(Id + " : " + subwindow[Id]);
//                    // 더블클릭 이벤트 처리
//
//                    //깊은복사
//                    JTextArea clone = new JTextArea(panel.getText());
//                    clone.setFont(panel.getFont());
//                    clone.setForeground(panel.getForeground());
//                    clone.setBackground(panel.getBackground());
//                    clone.setMinimumSize(new Dimension(500,400));
//                    clone.setLineWrap(true);
//                    clone.setWrapStyleWord(true);
//                    clone.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//                    showDialog(clone,Id,title);
//
//                    clickCount = 0;
//                } else {
//                    clickCount = 1;
//                }
//                lastClickTime = currentTime;
//            }
//        });
//    }
//    public void openSubWindow2(Editor editor,int Id, String title){
//
//        editor.addEditorMouseListener(new EditorMouseListener() {
//            @Override
//            public void mouseClicked(EditorMouseEvent e) {
//                //System.out.println(Id);
//                if (e.getArea() == EditorMouseEventArea.EDITING_AREA && e.getMouseEvent().getClickCount() == 2 && !subwindow[Id]) {
//                    //깊은복사
//                    Editor cloneEdiot = EditorFactory.getInstance().createEditor(editor.getDocument());
//                    cloneEdiot.getSettings().setUseSoftWraps(true);
//                    cloneEdiot.getSettings().setBlockCursor(true);
//                    cloneEdiot.getSettings().setFoldingOutlineShown(true);
//                    showDialog(cloneEdiot.getComponent(),Id,title);
//                }
//            }
//
//        });
//    }
//    public void showDialog(Component panel,int Id,String title) {
//        JFrame jFrame = new JFrame();
//        jFrame.setTitle(title);
//
//        jFrame.add(panel);
//        panel.setMinimumSize(new Dimension(500,500));
//        jFrame.setMinimumSize(new Dimension(500,500));
//        jFrame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                jFrame.dispose();
//                subwindow[Id] = false;
//            }
//        });
//        subwindow[Id] = true;
//        jFrame.setVisible(true);
//    }
//    private JPanel titlePanel(String title,Icon icon,String tip){
//        JPanel panel = new JPanel();
//        //panel.setBackground(Color.GREEN);
//        //title1.setBackground(new Color(244, 250, 252));
//        panel.setLayout(new GridBagLayout());
//        GridBagConstraints constraints=new GridBagConstraints();
//
//
//        JLabel commentTitle = new JLabel(title);
//        commentTitle.setBackground(Color.BLUE);
//        constraints = settingConst(constraints,0,0,-1,0.1,1,1,true);
//        panel.add(commentTitle,constraints);
//
//
//        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL); // 구분선
//        separator.setPreferredSize(new Dimension(200, 3)); // Set the height
//        separator.setForeground(new Color(100, 100, 100)); // Set the color
//
//        constraints = settingConst(constraints,0,1,-1,0.8,1,1,true);
//        JPanel sePanel = new JPanel(new BorderLayout());
//        sePanel.add(new JPanel(),BorderLayout.NORTH);
//        sePanel.add(separator,BorderLayout.CENTER);
//
//        panel.add(sePanel,constraints);
//
//        JButton btn = new JButton(icon); // 아이콘으로 바꿀수 있는지 //"주석 적용하기",
//
//        btn.setBorderPainted(false);
//        btn.setContentAreaFilled(false);
//        btn.setPreferredSize(new Dimension(20,20));
//        btn.setForeground(new Color(43, 45, 48));
//        btn.setToolTipText(tip);
//        btn.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                // 마우스가 버튼에 진입할 때의 동작
//                btn.setContentAreaFilled(true);
//            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                // 마우스가 버튼에서 나갈 때의 동작
//                btn.setContentAreaFilled(false);
//            }
//        });
////        btn.addActionListener(e -> {
////            String menu  = Messages.showInputDialog("저장 되었습니다.","문서화",Messages.getQuestionIcon());
////            Messages.showMessageDialog(menu,"저장저장",null);
////        });
//
//        constraints = settingConst(constraints,0,2,-1,0.1,1,1,false);
//        panel.add(btn,constraints);
//        return panel;
//    }
//    private JToggleButton toggleLock(JPanel codePanel,JToggleButton toggleButton,JToggleButton other ){
//        toggleButton.addActionListener(e -> {
//            if (toggleButton.isSelected() && !other.isSelected()) {
//                codePanel.setVisible(false);
//                toggleButton.setIcon(AllIcons.General.ArrowDown);
//
//
//            } else {
//                codePanel.setVisible(true);
//                toggleButton.setIcon(AllIcons.General.ArrowUp);
//            }
//        });
//        return toggleButton;
//    }
//    private JToggleButton togglePanel(JPanel codePanel){
//
//        JToggleButton togglePanel = new JToggleButton(AllIcons.General.ArrowUp); // 아이콘으로 바꿀수 있는지 //"주석 적용하기",
//
//        togglePanel.setBorderPainted(false);
//        togglePanel.setContentAreaFilled(false);
//        togglePanel.setPreferredSize(new Dimension(15,15));
//        togglePanel.setForeground(new Color(43, 45, 48));
//        togglePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//
//
//        togglePanel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                // 마우스가 버튼에 진입할 때의 동작
//                togglePanel.setContentAreaFilled(true);
//            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                // 마우스가 버튼에서 나갈 때의 동작
//                togglePanel.setContentAreaFilled(false);
//            }
//        });
//
//        return togglePanel;
//    }
//    private GridBagConstraints settingConst(GridBagConstraints cons,int gridy,int gridx,double weighty,double weightx,int gridheight,int gridwidth,boolean fill){
//        if(gridx != -1){
//            cons.gridx=gridx;
//        }
//        if(gridy != -1){
//            cons.gridy=gridy;
//        }
//        if(fill){
//            cons.fill = GridBagConstraints.BOTH;
//        }
//        if(weightx != -1){
//            cons.weightx = weightx;
//        }
//        if(weighty != -1){
//            cons.weighty = weighty;
//        }
//        if(gridheight != -1){
//            cons.gridheight = gridheight;
//        }
//        if(gridwidth != -1){
//            cons.gridwidth = gridwidth;
//        }
//        return cons;
//    }
//    private Editor setEditor(Editor editor,String name){
//        editor.getSettings().setUseSoftWraps(true);
//        editor.getSettings().setBlockCursor(true);
//        editor.getSettings().setFoldingOutlineShown(true);
//        editor.getComponent().setName(name);
//        return editor;
//    }
//    private JTextArea setJTextArea(JTextArea jTextArea, String name){
//        jTextArea.setLineWrap(true);
//        jTextArea.setWrapStyleWord(true);
//        jTextArea.setText(name);
//        //codeReview.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
//        jTextArea.setBackground(new Color(43, 43, 43));
//        jTextArea.setName(name);
//
//        return jTextArea;
//    }
//
//
//}
