package com.mobilesuit.clientplugin.gpt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.mobilesuit.clientplugin.gpt.repository.ClassToMethodsMapRepository;
import com.mobilesuit.clientplugin.gpt.repository.UncommitFile;
import com.mobilesuit.clientplugin.gpt.ui.CodeReviewSiderBarFactory;

import java.util.*;

public class CodeReviewSideBarAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);

        ClassToMethodsMapRepository.getInstance().getClassToMethodsMap().clear();
        Map<PsiClass, List<PsiMethod>> classToMethodsMapPsi;
        classToMethodsMapPsi = ClassToMethodsMapRepository.getInstance().getClassToMethodsMap();

        for (String className : cache.getAllClassNames()) {
            PsiClass[] psiClasses = cache.getClassesByName(className, scope);
            for (PsiClass psiClass : psiClasses) {

                if (psiClass.getContainingFile() instanceof PsiJavaFile) {
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
                    classToMethodsMapPsi.put(psiClass,psiMethods);
                }
            }
        }

        String toolWindowId = "CodeReviewSideBar";
        ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow(toolWindowId);
        //CodeReviewSiderBarFactory siderBarFactory = new CodeReviewSiderBarFactory();
        toolWindow.getContentManager().removeAllContents(true);
        CodeReviewSiderBarFactory.getInstance().createToolWindowContent(e.getProject(),toolWindow);

    }
}
