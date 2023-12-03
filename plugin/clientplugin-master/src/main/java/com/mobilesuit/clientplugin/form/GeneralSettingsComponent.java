package com.mobilesuit.clientplugin.form;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

@Slf4j
public class GeneralSettingsComponent {
    private JPanel mainPanel;
    private JPanel gptPanel;
    private JCheckBox useMyOwnOpenAICheckBox;
    private JTextField openAIKeyTextField;
    private JTextField notionAPIKeyTextField;
    private JTextField notionDatabaseIDTextField;
    private JTextField markdownFileStorageLocationTextField;
    private JButton selectDirectoryButton;
    private JCheckBox receiveCommitRecommendationCheckBox;
    private JFormattedTextField standardFileSizeChangeTextField;

    public GeneralSettingsComponent() {

        selectDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"))); // 홈 디렉토리를 파일 탐색기의 시작점으로 설정
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 파일 탐색기에서 디렉토리만 보여줌

                int result = fileChooser.showOpenDialog(mainPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    log.info("Selected directory: " + selectedDirectory.getAbsolutePath());

                    setMarkdownFileStorageLocationTextField(selectedDirectory.getAbsolutePath());
                }
            }
        });

        standardFileSizeChangeTextField.setFormatterFactory(new DefaultFormatterFactory(createNumberFormatter()));
    }

    public JPanel getContentPane() {
        return mainPanel;
    }

    public boolean getUseMyOwnOpenAICheckBox() {
        return useMyOwnOpenAICheckBox.isSelected();
    }

    public void setUseMyOwnOpenAICheckBox(boolean selected) {
        useMyOwnOpenAICheckBox.setSelected(selected);
    }

    public String getOpenAIKey() {
        return openAIKeyTextField.getText();
    }

    public void setOpenAIKey(String text) {
        openAIKeyTextField.setText(text);
    }

    public String getNotionAPIKey() {
        return notionAPIKeyTextField.getText();
    }

    public void setNotionAPIKey(String text) {
        notionAPIKeyTextField.setText(text);
    }

    public String getNotionDatabaseID() {
        return notionDatabaseIDTextField.getText();
    }

    public void setNotionDatabaseIDTextField(String text) {
        notionDatabaseIDTextField.setText(text);
    }

    public String getMarkdownFileStorageLocation() {
        return markdownFileStorageLocationTextField.getText();
    }

    public void setMarkdownFileStorageLocationTextField(String text) {
        markdownFileStorageLocationTextField.setText(text);
    }

    public boolean getReceiveCommitRecommendation() {
        return receiveCommitRecommendationCheckBox.isSelected();
    }

    public void setReceiveCommitRecommendationCheckBox(boolean selected) {
        receiveCommitRecommendationCheckBox.setSelected(selected);
    }

    public int getStandardFileSizeChange() {
        return Integer.parseInt(standardFileSizeChangeTextField.getText());
    }

    public void setStandardFileSizeChangeTextField(int value) {
        standardFileSizeChangeTextField.setText(String.valueOf(value));
    }

    private NumberFormatter createNumberFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        return formatter;
    }
}
