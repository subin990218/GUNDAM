package com.mobilesuit.clientplugin.form;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.ui.JBColor;
import com.mobilesuit.clientplugin.convention.CommitMessageEditorPanel;
import com.mobilesuit.clientplugin.convention.ConventionContainer;
import com.mobilesuit.clientplugin.util.WriteActionUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

@Slf4j
public class CommitMessageDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonBuild;
    private JButton buttonCancel;
    private JTextArea textArea;
    private JTextField textField;
    private JPanel editorPanel;
    private CheckinProjectPanel checkinProjectPanel;
    private ConventionContainer conventionUtil;

    public CommitMessageDialog(Project project, CheckinProjectPanel checkinProjectPanel) {
        this.checkinProjectPanel = checkinProjectPanel;
        this.conventionUtil = ConventionContainer.getInstance();
        conventionUtil.loadConvention(project);

        setTitle("Build Commit Message");
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon/GUNDAM.png"))).getImage());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonBuild);

        CommitMessageEditorPanel commitMessageEditorPanel = new CommitMessageEditorPanel(project, this);
        editorPanel.setLayout(new BorderLayout());
        editorPanel.add(commitMessageEditorPanel, BorderLayout.CENTER);

        WriteActionUtil.invokeLaterWriteAction(project, () -> {
            updateTextAreaText();
            setTextFieldText(conventionUtil.getCurrentPrompt());
        });

        textArea.setEnabled(false);
        textField.setEnabled(false);

        buttonBuild.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        checkinProjectPanel.setCommitMessage(conventionUtil.getConventionMessage());
        onCancel();
    }

    private void onCancel() {
        ((CommitMessageEditorPanel) editorPanel.getComponent(0)).dispose();
        dispose();
    }

    public void updateTextAreaText() {
        conventionUtil.updateConventionMessage();
        textArea.setText(conventionUtil.getConventionMessage());
        highlightTextArea(conventionUtil.getCurrentPlaceholderOffset(), conventionUtil.getCurrentPlaceholderLength());
    }

    public void setTextFieldText(String text) {
        textField.setText(text);
    }

    public void highlightTextArea(int offset, int length) {
        Highlighter highlighter = textArea.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(JBColor.GREEN);

        if (offset != -1) {
            try {
                highlighter.removeAllHighlights();
                highlighter.addHighlight(offset, offset + length, painter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
