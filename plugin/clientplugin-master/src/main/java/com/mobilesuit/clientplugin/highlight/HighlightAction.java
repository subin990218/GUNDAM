package com.mobilesuit.clientplugin.highlight;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ColorChooser;

import javax.swing.*;
import java.awt.*;

public class HighlightAction extends AnAction {

    Color[] colors = {new Color(246, 86, 86, 74),
            new Color(86, 91, 180, 100),
            new Color(54, 122, 67, 90),
            new Color(236, 208, 70, 66)};
    static int colorId =0;

    @Override
    public void actionPerformed(AnActionEvent e) {
        //show();
        Project project = e.getProject();

        if (project != null) {
            Editor editor = e.getData(CommonDataKeys.EDITOR);

            if (editor != null) {
                Caret caret = editor.getCaretModel().getPrimaryCaret();
                int start = caret.getSelectionStart();
                int end = caret.getSelectionEnd();

                if(start == end){
                    colorId = (colorId+1)%4;
                    return;
                }

                if(!deleteHighlight(editor,start,end)){// false일때만 색칠을 진행한다.
                    applyCustomHighlighting(editor, start,end);
                }
                editor.getContentComponent().repaint();
            }
        }
    }

    private boolean deleteHighlight(Editor editor,int start, int end){
        // 새로 하이라이트 하려는 구간이 이미 하이라이트 되어있는지 확인
        // 그렇다 -> 그부분만 하이라이트를 지운다, 나머지 부분도 칠하지 않는다.
        boolean flag = false; // true는 하나라도 겹치는 경우 이므로 더이상 색칠은 하지 않도록한다.

        MarkupModel markupModel = editor.getMarkupModel();
        int editorStart = start;
        int editorEnd = end;

        for(int lineNumber=editorStart;lineNumber<=editorEnd;lineNumber++){ // 각 라인에 대해 검사한다.

            for (RangeHighlighter highlighter : markupModel.getAllHighlighters()) {
                if (highlighter.getStartOffset() <= lineNumber && highlighter.getEndOffset() >= lineNumber) {
                    markupModel.removeHighlighter(highlighter); // 각 줄에 대해서만 지운다.
                    flag = true;
                }
            }
        }
        return flag;
    }
    private void applyCustomHighlighting(Editor editor, int start, int end) {
        TextAttributes attribute= setAttribute();
        MarkupModel markupModel = editor.getMarkupModel();

        int startLine = editor.getDocument().getLineNumber(start);
        int endLine = editor.getDocument().getLineNumber(end);
        for(int line =startLine; line <= endLine;line++){
            RangeHighlighter highlighter = ((MarkupModelEx) markupModel).addLineHighlighter(line,0,attribute);
        }
        editor.getContentComponent().repaint();
    }

    private TextAttributes setAttribute(){
        TextAttributes textAttributes = new TextAttributes();
        textAttributes.setBackgroundColor(colors[colorId]); // 배경 색상을 노란색으로 설정
//        textAttributes.setForegroundColor(Color.BLUE); // 텍스트 색상을 파란색으로 설정
//        textAttributes.setEffectColor(Color.RED); // 효과 색상을 빨간색으로 설정

        return textAttributes;
    }



}
