package com.mobilesuit.clientplugin.gpt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.mobilesuit.clientplugin.gpt.repository.ClassToMethodsMapRepository;
import com.mobilesuit.clientplugin.gpt.repository.UncommitFile;
import com.mobilesuit.clientplugin.gpt.ui.CodeReviewWindow;

import java.util.*;

public class CodeReviewChangeAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        List<VirtualFile> uncommitFileList = UncommitFile.getChangeFileList(project);

        //Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi = codeClassification(project,uncommitFileList);

        Map<String, List<String>> classToMethodsMap = new HashMap<>();
        Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi = new HashMap<>();

        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile file : uncommitFileList) {
            PsiFile psiFile = psiManager.findFile(file);

            if (psiFile instanceof PsiJavaFile) { // We are only interested in Java files
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                for (PsiClass psiClass : psiJavaFile.getClasses()) {
                    boolean hasLombokAnnotation = Arrays.stream(psiClass.getAnnotations())
                            .map(PsiAnnotation::getQualifiedName)
                            .anyMatch(annoQualifiedName -> annoQualifiedName != null &&
                                    (annoQualifiedName.startsWith("lombok.")));


                    List<String> methodNames = new ArrayList<>();
                    List<PsiMethod> psiMethods = new ArrayList<>();

                    for (PsiMethod psiMethod : psiClass.getMethods()) {
                        String methodName = psiMethod.getName();
                        boolean isLombokGenerated = hasLombokAnnotation && methodName.startsWith("get") ||
                                methodName.startsWith("set") ||
                                methodName.equals(psiClass.getName()) ||
                                methodName.equals("toString") ||
                                methodName.equals("hashCode") ||
                                methodName.equals("equals");

                        if (!isLombokGenerated){
                            // 롬복 메소드가 아닌 경우에만 리스트에 추가합니다.
                            // 커스텀 생성자, get ,set도 삭제 시켜 버림
                            methodNames.add(methodName);
                            psiMethods.add(psiMethod);
                        }
                    }
                    classToMethodsMapPsi.put(psiClass, psiMethods);
                    classToMethodsMap.put(psiClass.getName(), methodNames);
                }
            }
        }


        CodeReviewWindow program3 = new CodeReviewWindow(classToMethodsMapPsi, project);
    }//actionPerfome end

    public Map<PsiClass, List<PsiMethod>> codeClassification(Project project, List<VirtualFile> uncommitFileList){
        ClassToMethodsMapRepository.getInstance().getClassToMethodsMap().clear();
        Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi;
        classToMethodsMapPsi = ClassToMethodsMapRepository.getInstance().getClassToMethodsMap();

        Map<String, List<String>> classToMethodsMap = new HashMap<>();

        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile file : uncommitFileList) {
            PsiFile psiFile = psiManager.findFile(file);

            if (psiFile instanceof PsiJavaFile) { // We are only interested in Java files
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                for (PsiClass psiClass : psiJavaFile.getClasses()) {
                    boolean hasLombokAnnotation = Arrays.stream(psiClass.getAnnotations())
                            .map(PsiAnnotation::getQualifiedName)
                            .anyMatch(annoQualifiedName -> annoQualifiedName != null &&
                                    (annoQualifiedName.startsWith("lombok.")));


                    List<String> methodNames = new ArrayList<>();
                    List<PsiMethod> psiMethods = new ArrayList<>();

                    for (PsiMethod psiMethod : psiClass.getMethods()) {
                        String methodName = psiMethod.getName();
                        boolean isLombokGenerated = hasLombokAnnotation && methodName.startsWith("get") ||
                                methodName.startsWith("set") ||
                                methodName.equals(psiClass.getName()) ||
                                methodName.equals("toString") ||
                                methodName.equals("hashCode") ||
                                methodName.equals("equals");

                        if (!isLombokGenerated){
                            // 롬복 메소드가 아닌 경우에만 리스트에 추가합니다.
                            // 커스텀 생성자, get ,set도 삭제 시켜 버림
                            methodNames.add(methodName);
                            psiMethods.add(psiMethod);
                        }
                    }
                    classToMethodsMapPsi.put(psiClass, psiMethods);
                    classToMethodsMap.put(psiClass.getName(), methodNames);
                }
            }
        }

        return classToMethodsMapPsi;
    }

}
