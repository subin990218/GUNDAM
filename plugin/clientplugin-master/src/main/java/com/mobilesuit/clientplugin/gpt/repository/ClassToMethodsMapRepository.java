package com.mobilesuit.clientplugin.gpt.repository;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassToMethodsMapRepository {
    private static ClassToMethodsMapRepository instance;
    private Map<PsiClass, List<PsiMethod>> classToMethodsMap;

    private ClassToMethodsMapRepository() {
        classToMethodsMap = new HashMap<>();
    }

    public static ClassToMethodsMapRepository getInstance() {
        if (instance == null) {
            instance = new ClassToMethodsMapRepository();
        }
        return instance;
    }

    public Map<PsiClass, List<PsiMethod>> getClassToMethodsMap() {
        return classToMethodsMap;
    }

    public void setClassToMethodsMap(Map<PsiClass, List<PsiMethod>> map) {
        this.classToMethodsMap = map;
    }
}
