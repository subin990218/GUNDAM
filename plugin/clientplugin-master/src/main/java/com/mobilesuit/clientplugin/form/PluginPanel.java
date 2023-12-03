package com.mobilesuit.clientplugin.form;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PluginPanel extends JPanel{
    private JPanel panel1;
    private JTextArea logArea;
    private JTextArea liveArea;
    private JButton clearButton;

    public PluginPanel() {
        this.setVisible(true);
        this.add(panel1);
        panel1.setVisible(true);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
            }
        });
    }

    public void appendText(String text){
        logArea.append(text+"\n");
    }

    public void updateUser(String text){
        liveArea.setText(text);
    }
}