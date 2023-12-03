package com.mobilesuit.clientplugin.gpt.ui;

import javax.swing.*;

public class PsiCheckBox extends JCheckBox {
    private Object psiObject;
    public PsiCheckBox(String text, Object psiObject) {
        super(text);
        this.psiObject = psiObject;
    }
    public PsiCheckBox() {
        this.psiObject = null;
    }

    public void setPsiObject(Object psiObject){
        this.psiObject =psiObject;
    }
    public Object getPsiObject() {
        return psiObject;
    }
}
