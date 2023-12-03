package com.mobilesuit.clientplugin.singleton;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.mobilesuit.clientplugin.form.PluginPanel;
import com.mobilesuit.clientplugin.renderer.AlertPanel;
import com.mobilesuit.clientplugin.renderer.CirclePanel;
import com.mobilesuit.clientplugin.renderer.LivePanel;
import com.mobilesuit.clientplugin.setting.MainSettingsConfigurable;
import lombok.Getter;
import lombok.Setter;
import okhttp3.internal.SuppressSignatureCheck;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DataContainer {
    private static final DataContainer instance = new DataContainer();

    @Getter
    @Setter
    private String projectPath;

    @Getter
    @Setter
    private String projectPathBackSlash;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private JFrame mainFrame;

    @Getter
    @Setter
    private JPanel drawPanel;

    @Getter
    @Setter
    private JDialog overlayFrame;

    @Getter
    @Setter
    private JLabel imageLabel;

    @Getter
    @Setter
    private LivePanel livePanel;

    @Getter
    @Setter
    private Map<String,String> userList = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private Map<String,String> userStatus = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private Map<String, CirclePanel> circlePanelMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private Map<String, Color> colorMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private Map<String, Set<String>> fileMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private Boolean starter;

    @Getter
    @Setter
    private String lastFile;

    @Getter
    @Setter
    private String gitHubAccessToken;

    @Getter
    @Setter
    private String endPath;

    @Getter
    @Setter
    private String userId;

    @Getter
    @Setter
    private String userAvatarUrl;

    @Getter
    @Setter
    private Boolean ctrlPressed;

    @Getter
    @Setter
    private Boolean mousePressed;

    @Getter
    @Setter
    private Point startPoint;

    @Getter
    @Setter
    private AlertPanel alertPanel;

    @Getter
    @Setter
    private AlertPanel pingShowPanel;

    @Getter
    @Setter
    private MainSettingsConfigurable mainSettingsConfigurable;

    @Getter
    @Setter
    private Timer editorTimer;

    @Getter
    @Setter
    private PluginPanel pluginPanel;

    private DataContainer(){
        starter = false;
    }

    public static DataContainer getInstance(){
        return instance;
    }

    public JFrame updateMainFrame(){
        return mainFrame = WindowManager.getInstance().getFrame(this.getProject());
    }

    public void logOutUser(String userName){
        if(userList.containsKey(userName)) {
            userList.remove(userName);
        }
        if(userStatus.containsKey(userName)) {
            userStatus.remove(userName);
        }
        if(colorMap.containsKey(userName)) {
            colorMap.remove(userName);
        }
        for(String path : fileMap.keySet()){
            fileMap.get(path).remove(userName);
        }
    }

    public void moveToFile(String filePath, String userName){
        if(userList.containsKey(userName)) {
            String prevPath = userList.get(userName);
            userList.put(userName,filePath);
            if(fileMap.containsKey(prevPath)){
                fileMap.get(prevPath).remove(userName);
            }
        }else{
            userList.put(userName,filePath);
        }
        if(!fileMap.containsKey(filePath)){
            fileMap.put(filePath,new ConcurrentSkipListSet<>());
            fileMap.get(filePath).add(userName);
        }else{
            fileMap.get(filePath).add(userName);
        }

        /*for(String path : fileMap.keySet()){
            System.out.printf("["+path+"] : ");
            for(String user : fileMap.get(path)){
                System.out.printf(user+ ", ");
            }
            System.out.println();
        }*/
        StringBuilder sb=  new StringBuilder();
        for(String user : userList.keySet()){
            sb.append(user).append(" in ").append(userList.get(user)).append("\n");
        }
        pluginPanel.updateUser(sb.toString());
    }

    public void addColorMap(String userName){
        if(!colorMap.containsKey(userName)){
            Random randomColorGenerator= new Random();
            float r= randomColorGenerator.nextFloat();
            float g= randomColorGenerator.nextFloat();
            float b= randomColorGenerator.nextFloat();

            Color color = new Color(r,g,b);
            colorMap.put(userName,color);
        }
    }
}
