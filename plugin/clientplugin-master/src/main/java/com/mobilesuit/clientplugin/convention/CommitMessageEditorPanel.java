package com.mobilesuit.clientplugin.convention;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.mobilesuit.clientplugin.form.CommitMessageDialog;
import com.mobilesuit.clientplugin.util.WriteActionUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

@Slf4j
public class CommitMessageEditorPanel extends JBPanel {
    @Getter
    private Editor editor;
    private VirtualFile tempFile;
    private final Project project;
    private final ConventionContainer conventionUtil;
    private final CommitMessageDialog commitMessageDialog;
    private boolean isPluginAction;

    public CommitMessageEditorPanel(Project project, CommitMessageDialog commitMessageDialog) {
        this.project = project;
        this.conventionUtil = ConventionContainer.getInstance();
        this.commitMessageDialog = commitMessageDialog;
        setLayout(new BorderLayout());

        try {
            this.tempFile = createTempFile();
            Document document = createDocumentForFile(tempFile);
            editor = EditorFactory.getInstance().createEditor(document, project);
            editor.getSettings().setLineNumbersShown(false);
            editor.getSettings().setLineMarkerAreaShown(false);
            SwingUtilities.invokeLater(() -> {
                editor.getContentComponent().requestFocusInWindow();
            });

            document.setReadOnly(false);

            add(editor.getComponent(), BorderLayout.CENTER);

            setupKeyListener();
            setupDocumentListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupKeyListener() {
        editor.getContentComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        onAltRightEvent();
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        onAltLeftEvent();
                    }
                }
            }
        });
    }

    private void setupDocumentListener() {
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent event) {
                if (isPluginAction) {
                    return;
                }
                onUserInputEvent();
            }
        });
    }

    private void onUserInputEvent() {
        conventionUtil.setCurrentUserInputFlag(Boolean.TRUE);
        conventionUtil.setCurrentUserInput(editor.getDocument().getText());
        WriteActionUtil.invokeLaterWriteAction(project, () -> {
            try {
                commitMessageDialog.updateTextAreaText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void onAltRightEvent() {
        conventionUtil.increaseCurrentIndex();
        conventionUtil.setPreviousUserInputFlag(Boolean.TRUE);
        changeEditorText();
        WriteActionUtil.invokeLaterWriteAction(project, () -> {
            try {
                commitMessageDialog.updateTextAreaText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void onAltLeftEvent() {
        conventionUtil.decreaseCurrentIndex();
        conventionUtil.setPreviousUserInputFlag(Boolean.TRUE);
        changeEditorText();
        WriteActionUtil.invokeLaterWriteAction(project, () -> {
            try {
                commitMessageDialog.updateTextAreaText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void changeEditorText() {
        WriteActionUtil.invokeLaterWriteAction(project, () -> {
            try {
                isPluginAction = true;
                editor.getDocument().setText(conventionUtil.getCurrentUserInput());
                commitMessageDialog.setTextFieldText(conventionUtil.getCurrentPrompt());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isPluginAction = false;
            }
        });
    }

    public void dispose() {
        EditorFactory.getInstance().releaseEditor(editor);
        if (this.tempFile == null) {
            return;
        }
        deleteVirtualFile(this.project, this.tempFile);
    }

    public VirtualFile createTempFile() throws IOException {
        String tempFileName = "gundam_convention_temp";
        String tempFileExtension = ".txt";
        String tempDirPath = System.getProperty("java.io.tmpdir");

        String tempFilePath = tempDirPath + File.separator + tempFileName + tempFileExtension;

        VirtualFile existingFile = LocalFileSystem.getInstance().findFileByPath(tempFilePath);

        if (existingFile != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    existingFile.delete(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        File file = FileUtil.createTempFile(tempFileName, tempFileExtension, true);

        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
    }

    public void deleteVirtualFile(Project project, VirtualFile fileToDelete) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                fileToDelete.delete(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Document createDocumentForFile(VirtualFile virtualFile) {
        return FileDocumentManager.getInstance().getDocument(virtualFile);
    }
}
