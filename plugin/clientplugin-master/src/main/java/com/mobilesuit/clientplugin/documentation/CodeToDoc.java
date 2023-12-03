package com.mobilesuit.clientplugin.documentation;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.mobilesuit.clientplugin.documentation.dto.Doc;
import com.mobilesuit.clientplugin.documentation.dto.Method;
import com.mobilesuit.clientplugin.documentation.dto.TypeSet;
import com.mobilesuit.clientplugin.gpt.repository.CodeInfo;
import com.mobilesuit.clientplugin.setting.GeneralSettingsState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeToDoc {
    //파라미터 값을 미리 찾아 주어야 한다.
    private static CodeToDoc instance = new CodeToDoc();
    private CodeToDoc(){}
    private static CodeInfo codeInfo;
    private static String commentedCode;
    private static String codeReview;
    private static String cleanCode;
    private static StringBuilder allContent;
    private static String filePath;
    //단일 메서드 입력에 대해서만 사용되는 변수
    private static PsiParameter[] paramList;
    private static String returnType;
    private static String methodTitle;
    //클래스에 대해서만 사용되는 변수
    private static PsiMethod[] methodList;
    private static List<String> methodNameList;
    private static PsiField[] classPsiField;
    //저장할 사용자 정보
    private static String userId;
    private static String repoName;
    private static SendMainServer sendMainServer = SendMainServer.getInstance();
    public static CodeToDoc getInstance
            (CodeInfo info, String code, String Review,
             String clean, PsiMethod method,PsiField[] PsiField, String path,String repo,String user){//, PsiParameter[] param, String returnValue){

        methodNameList = new ArrayList<>();
        allContent = new StringBuilder();
        commentedCode = code;
        //System.out.println(" Code To Doc - commentenCode :" + commentedCode);
        codeReview = changeCodeReview(Review);
        cleanCode = clean;
        codeInfo = info;
        filePath = path;
        userId = user;
        repoName = repo;
        //클래스 일때만 들어오는 데이터
        if(PsiField != null){
            for (PsiField field : PsiField) {

                System.out.println( "s getType : "+ field.getType());
                System.out.println("s getName :  " + field.getName());
            }
            classPsiField = PsiField;

        }else{
            classPsiField = null;
        }
        //메서드 일때만 들어오는 데이터
        if(method != null){
            paramList = method.getParameterList().getParameters();
            returnType = method.getReturnType().toString().replace("PsiType:","");
        }else{
            paramList = null;
            returnType = null;
        }
        return instance;
    }
    public static String changeCodeReview(String review){
        String [] str = review.split("\\n");
        StringBuilder sb = new StringBuilder();
        for(String s:str){
            sb.append("> ").append(s).append("\n");
        }
        return sb.toString();
    }
    public int makeMarkDownDoc(){ //정상시 1 비정상종료 0
        int res =0;
        // 파일을 새로 만들고, 열고, 쓰고 닫는다.
        File tmp = new File(codeInfo.getFilePath());
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(tmp);

        Project project = ProjectUtil.guessProjectForFile(virtualFile);

        GeneralSettingsState SettingsState = GeneralSettingsState.getInstance(project);
        //String filePath = project.getBasePath() + "/CodeDocument";
        String filePath = SettingsState.getMarkdownFileStorageLocation();

        if(filePath.equals("")){
            filePath = project.getBasePath() + "/CodeDocument";
        }

        try{

            File folder = new File(filePath);
            if (!folder.exists()) {
                if (folder.mkdirs()){
                    System.out.println("저장할 폴더가 없다면 생성");
                }else{
                    // 파일을 지정한나면 여기서 폴더를 만들라한다.
                    Messages.showErrorDialog("이 에러가 반복되는 경우\n 파일 저장경로가 올바른지 확인 하세요", "파일 생성 오류");
                    return res;
                }
            }

            String fileName = codeInfo.getName();
            if(codeInfo.getType().equals("class")){ //클래스의 경우
                toClassDoc();
                //fileName = codeInfo.getName();
            }else{ //메서드의 경우
                toMethodDoc();
                //fileName = codeInfo.getName();
            }

            String fileAddress = filePath+"/"+fileName+".md";
            //System.out.println("fileAddress : " + fileAddress);
            File codedoc = new File(fileAddress);
            if (codedoc.exists()) {
                int result = Messages.showYesNoCancelDialog(
                        "로컬에 같은 이름의 문서가 있습니다.\n 덮어쓰시겠습니까?\n (yes: 덮어쓰기, no: 파일명변경, cancel: 문서화 취소)",
                        "덮어쓰기",
                        Messages.getQuestionIcon()
                );
                if (result == Messages.YES) {
                    // 덮어쓰기
                } else if (result == Messages.NO) {
                    // 파일명 변경
                    String userInput  = Messages.showInputDialog("문서를 저장할 파일명을 입력하세요\n(확장자 명은 작성하지 않습니다.)","파일명",Messages.getQuestionIcon());
                    if (userInput != null) {
                        fileAddress = filePath+"/"+userInput+".md";
                    }
                } else {
                    //문서화 취소
                    Messages.showInfoMessage("마크다운 문서화가 취소되었습니다.", "마크다운 문서화");
                    return res;
                }

                System.out.println("이미 작성된 문서가 있습니다. 덮어쓰시겠습니까? 네"); //메시지 띄우기
            }

            FileWriter writer = new FileWriter(fileAddress);
            writer.write(allContent.toString());
            writer.close();
            System.out.println("마크다운 문서 작성 완료");

        }catch(IOException ioe){
            System.out.println("마크다운 문서 작성 에러");
            ioe.printStackTrace();
            return res;
        }
        return 1;
    }
    public void toMethodDoc(){

        //Doc doc = new Doc();

        String enter = "\n";
        String line = "* * *\n";
        String box = "> ";
        String toggelStart1 = "<details><summary>";
        String toggelStart2 = "</summary><div markdown=\"1\">";
        String toggelEnd = "</div></details>\n";
        String javaCodeStart = " ```java ";
        String javaCodeEnd = " ``` ";
        String point = "- ";

        //0. Main Title
        methodTitle = getMethodName(codeInfo.getName(),paramList);
        allContent.append("# [Method] ").append(methodTitle).append(enter).append(line);

        //1. Code Review
        //allContent.append("## 1. Code Review").append(enter).append(box).append(codeReview);
        allContent.append("## 1. Code Review").append(enter).append(codeReview);
        allContent.append("\n").append("\n");

        //2. Source Code
        allContent.append("## 2. Source Code").append(enter);
        // Commented code
        allContent.append(toggelStart1).append(" Commented Code ").append(toggelStart2).append(enter).append(enter).append("## Commented Code").append(enter).append(line);
        allContent.append(javaCodeStart).append(enter).append(commentedCode).append(enter).append(javaCodeEnd).append(enter).append(toggelEnd);
        // Clean Code
        allContent.append(toggelStart1).append(" Clean Code ").append(toggelStart2).append(enter).append(enter).append("## Clean Code").append(enter).append(line);
        allContent.append(javaCodeStart).append(cleanCode).append(enter).append(javaCodeEnd).append(enter).append(toggelEnd).append(enter);

        //3. Return Type
        allContent.append("## 3. Return Type").append(enter);
        allContent.append(point).append("`").append(returnType).append("`").append(enter); // 수정 필요

        //4. Parameter Type
        allContent.append("## 4. Parameter Type").append(enter);
        String parameterTable = makeParamTable(paramList);
        allContent.append(parameterTable).append(enter).append(line).append(enter);

//        List<TypeSet> typeSetList = new ArrayList<>();
//        for(PsiParameter param : paramList){
//            TypeSet typeSet = new TypeSet(param.getType().toString().replace("PsiType:",""),param.getName(),0);
//            typeSetList.add(typeSet);
//        }
//        doc.setMPList(typeSetList);
//
//        //DB에 저장
//        doc.setRepoName(repoName);
//        doc.setUserId(userId);
//        doc.setFilePath(filePath);
//        doc.setName(methodTitle);
//        doc.setType("method");
//        doc.setCodeReview(codeReview);
//        doc.setCode(commentedCode);
//        doc.setCleanCode(cleanCode);
//        doc.setReturnType(returnType); // 없다.
//        doc.setMethodList(null);
//
//
//        sendMainServer.saveDocElement(doc);
    }
    public void methodToServer(){
        methodTitle = getMethodName(codeInfo.getName(),paramList);

        Doc doc = new Doc();
        List<TypeSet> typeSetList = new ArrayList<>();
        for(PsiParameter param : paramList){
            TypeSet typeSet = new TypeSet(param.getType().toString().replace("PsiType:",""),param.getName(),0);
            typeSetList.add(typeSet);
        }
        doc.setMPList(typeSetList);

        //DB에 저장
        doc.setRepoName(repoName);
        doc.setUserId(userId);
        doc.setFilePath(filePath);
        doc.setName(methodTitle);
        doc.setType("method");
        doc.setCodeReview(codeReview);
        doc.setCode(commentedCode);
        doc.setCleanCode(cleanCode);
        doc.setReturnType(returnType); // 없다.
        doc.setMethodList(null);

        sendMainServer.saveDocElement(doc);
    }
    public void toClassDoc(){

        //Doc doc = new Doc();

        String enter = "\n";
        String line = "* * *";
        String box = "> ";
        String toggelStart1 = "<details><summary>";
        String toggelStart2 = "</summary><div markdown=\"1\">";
        String toggelEnd = "</div></details>";
        String javaCodeStart = " ```java ";
        String javaCodeEnd = " ``` ";
        String point = "- ";

        //0. Main Title
        allContent.append("# [Class] ").append(codeInfo.getName()).append(enter).append(line).append(enter);

        //1. Code Review
        //allContent.append("## 1. Code Review").append(enter).append(box).append(codeReview);
        allContent.append("## 1. Code Review").append(enter).append(codeReview);
        allContent.append("\n").append("\n");

        //2. Navigation
        allContent.append("## 2. Navigation").append(enter);
        String navigation = getNavigation();
        allContent.append(navigation).append(enter);

        //3. Source Code
        allContent.append("## 3. Source Code").append(enter);

        // Commented code
        allContent.append(toggelStart1).append(" Commented Code ").append(toggelStart2).append(enter).append(enter).append("## Commented Code").append(enter).append(line).append(enter);
        allContent.append(javaCodeStart).append(enter).append(commentedCode).append(enter).append(javaCodeEnd).append(enter).append(toggelEnd);
        // Clean Code
        allContent.append(toggelStart1).append(" Clean Code ").append(toggelStart2).append(enter).append(enter).append("## Clean Code").append(enter).append(line).append(enter);
        allContent.append(javaCodeStart).append(enter).append(cleanCode).append(enter).append(javaCodeEnd).append(enter).append(toggelEnd).append(enter).append(enter);

        int n=4;
        if(classPsiField != null) {
            //4. Member Fields
            allContent.append("## 4. Member Fields").append(enter);
            String fieldListTable = getFieldTable(classPsiField);
            allContent.append(fieldListTable).append(enter).append(enter);
            n=5;
        }

        List<Method> methods = new ArrayList<>();
        //5. methodList
        for(int i=0;i<methodNameList.size();i++){
            //Method method = new Method();
            //5-1 method Title
            allContent.append("## ").append(i+n).append(". ").append(methodNameList.get(i));
            allContent.append(enter).append(line).append(enter);
            //method.setName(methodNameList.get(i));

            // 5-2 return Type
            String methodReturnType="null"; // 생성자의 경우
            if(methodList[i].getReturnType()!=null) {
                methodReturnType = methodList[i].getReturnType().toString().replace("PsiType:", "");
            }
            //method.setReturnType(methodReturnType);

            allContent.append("### Return Type").append(enter);
            allContent.append(point).append("`").append(returnType).append("`").append(enter); // 수정 필요


            // 5-3 parameter Type
            PsiParameter[] params = methodList[i].getParameterList().getParameters();
            allContent.append("### Parameter Type").append(enter);
            String parameterTable = makeParamTable(params);
            allContent.append(parameterTable).append(enter);

//            List<TypeSet> typeSetList = new ArrayList<>();
//            for(PsiParameter param : params){
//                TypeSet typeSet = new TypeSet(param.getType().toString().replace("PsiType:",""),param.getName(),0);
//                typeSetList.add(typeSet);
//            }
            //method.setParamList(typeSetList);
            //methods.add(method);

        }
        //doc.setMethodList(methods);
        allContent.append(line).append(enter);

        //DB에 저장
//        List<TypeSet> typeSetList = new ArrayList<>();
//        for( PsiField field: classPsiField){
//            TypeSet typeSet = new TypeSet(field.getType().toString().replace("PsiType:",""),field.getName(),0);
//            typeSetList.add(typeSet);
//        }
//        doc.setMPList(typeSetList);
//
//        doc.setRepoName(repoName);
//        doc.setUserId(userId);
//        doc.setFilePath(filePath);
//        doc.setName(codeInfo.getName());
//        doc.setType("class");
//        doc.setCodeReview(codeReview);
//        doc.setCode(commentedCode);
//        doc.setCleanCode(cleanCode);
//        doc.setReturnType(null); // 없다.
//
//
//        sendMainServer.saveDocElement(doc);

    }
    public void classToServer(){
        Doc doc = new Doc();

        List<Method> methods = new ArrayList<>();
        for(int i=0;i<methodNameList.size();i++){
            Method method = new Method();
            method.setName(methodNameList.get(i));

            // 5-2 return Type
            String methodReturnType="null"; // 생성자의 경우
            if(methodList[i].getReturnType()!=null) {
                methodReturnType = methodList[i].getReturnType().toString().replace("PsiType:", "");
            }
            method.setReturnType(methodReturnType);


            // 5-3 parameter Type
            PsiParameter[] params = methodList[i].getParameterList().getParameters();
            List<TypeSet> typeSetList = new ArrayList<>();
            for(PsiParameter param : params){
                TypeSet typeSet = new TypeSet(param.getType().toString().replace("PsiType:",""),param.getName(),0);
                typeSetList.add(typeSet);
            }
            method.setParamList(typeSetList);
            methods.add(method);

        }
        doc.setMethodList(methods);

        List<TypeSet> typeSetList = new ArrayList<>();
        for( PsiField field: classPsiField){
            TypeSet typeSet = new TypeSet(field.getType().toString().replace("PsiType:",""),field.getName(),0);
            typeSetList.add(typeSet);
        }
        doc.setMPList(typeSetList);

        doc.setRepoName(repoName);
        doc.setUserId(userId);
        doc.setFilePath(filePath);
        doc.setName(codeInfo.getName());
        doc.setType("class");
        doc.setCodeReview(codeReview);
        doc.setCode(commentedCode);
        doc.setCleanCode(cleanCode);
        doc.setReturnType(null); // 없다.


        sendMainServer.saveDocElement(doc);
    }
    private String makeParamTable(PsiParameter[] params){
        StringBuilder table = new StringBuilder();
        String enter = "\n";
        int paramSize = params.length;

        table.append("param number | type | variable name ").append("\n").append(":-:|:---:|:---:").append("\n");
        for (int i=0;i<paramSize;i++){
            table.append(i+1).append(" |`").append(params[i].getType().toString().replace("PsiType:","")).append("`|`").append(params[i].getName()).append("`").append(enter);
        }
        return table.toString();

    }
    public String getMethodName(String methodName,PsiParameter[] params){
        StringBuilder name = new StringBuilder(methodName);

        int paramSize = params.length;
        name.append("(");

        for (int i=0;i<paramSize;i++){
            String type = params[i].getType().toString().replace("PsiType:","");
            name.append(type).append(" ").append(params[i].getName());
            if(i < paramSize-1){name.append(", ");}
        }
        name.append(")");

        return name.toString();
    }
    private String getNavigation(){
        StringBuilder navi = new StringBuilder();
        methodList = getMethodList();
        int methodNum = methodList.length;
        String enter = "\n";

        StringBuilder sb = new StringBuilder();
        navi.append("* [Source Code](#3-source-code)").append(enter);
        for(int i=0;i<methodNum;i++){
            PsiParameter[] params = methodList[i].getParameterList().getParameters();
            String methodName = getMethodName(methodList[i].getName(),params);
            methodNameList.add(methodName);
            String address = convert(methodName);

            navi.append("* [").append(methodName).append("]").append("(#").append(i+5).append("-");
            navi.append(address).append(")").append(enter);

        }
        return navi.toString();
    }
    private String convert(String name){
        //printMap(int a, int b)
        //printmapint-a-int-b
        String address;
        name = name.replace("(","");
        name = name.replace(")","");
        name = name.replace(",","");
        name = name.replace(" ","-");
        address = name.toLowerCase();
        return address;
    }
    private String getFieldTable(PsiField[] field){

        StringBuilder table = new StringBuilder();
        int fieldSize = field.length;
        String enter = "\n";
        table.append("member number | type | variable name ").append("\n").append(":-:|:---:|:---:").append("\n");
        for (int i=0;i<fieldSize;i++){
            table.append(i+1).append(" |`").append(field[i].getType().toString().replace("PsiType:","")).append("`|`").append(field[i].getName()).append("`").append(enter);
        }
        return table.toString();


    }
    private PsiMethod[] getMethodList(){ //입력이 클래스일때 모든 메서드를 가져온다.

        PsiMethod[] methods = null;

        File file = new File(codeInfo.getFilePath());
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);

        Project project = ProjectUtil.guessProjectForFile(virtualFile);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        PsiJavaFile javaFile = (PsiJavaFile) psiFile;

        PsiClass[] classes = javaFile.getClasses();
        for (PsiClass psiClass : classes) {
            if(psiClass.getName().equals(codeInfo.getName())){
                methods = psiClass.getMethods();
            }
        }
        return methods;
    }

}
