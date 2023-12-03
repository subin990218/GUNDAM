package com.mobilesuit.clientplugin.form;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.mobilesuit.clientplugin.appservice.PluginService;
import com.mobilesuit.clientplugin.client.OauthClientServer;
import com.mobilesuit.clientplugin.client.SocketClient;
import com.mobilesuit.clientplugin.event.handler.EventHandler;
import com.mobilesuit.clientplugin.event.service.FileChangeDetector;
import com.mobilesuit.clientplugin.oauth.service.ApiService;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.util.SecureUtil;

import lombok.Getter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PluginOption extends JPanel {
    private final DataContainer dataContainer = DataContainer.getInstance();
    private final EventHandler eventHandler = EventHandler.getInstance();
    private final SocketClient socketClient = SocketClient.getInstance();
    private final ApiService apiService = ApiService.getInstance();
    private final OauthClientServer oauthClientServer = OauthClientServer.getInstance();

    private final PluginService pluginService = ApplicationManager.getApplication().getService(PluginService.class);


    private final CardLayout cardLayout = new CardLayout();
    private JButton loginButton;
    private JPanel mainPanel;
    private JButton deleteButton;

    @Getter
    private final PluginPanel pluginPanel;

    public PluginOption(Project project) {
        PluginOption pluginOption = this;
        pluginPanel = new PluginPanel();
        URL imageUrl = getClass().getResource("/images/github-mark.png");
        try {
            Image resizedImage = ImageIO.read(imageUrl).getScaledInstance(60,60,java.awt.Image.SCALE_SMOOTH);

            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            loginButton.setIcon(resizedIcon);
        }catch (IOException e){
            e.printStackTrace();
        }
        this.setLayout(cardLayout);
        this.add(mainPanel, "mainPanel");
        this.add(pluginPanel,"pluginPanel");


        this.setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pluginService.getHasRun().compareAndSet(false,true)) {
                    pluginService.runService(project,pluginOption);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SecureUtil.deleteToken();
            }
        });
    }
    public void remove(){
        this.remove(mainPanel);
        cardLayout.show(this, "pluginPanel");
        pluginPanel.setVisible(true);
    }


}
