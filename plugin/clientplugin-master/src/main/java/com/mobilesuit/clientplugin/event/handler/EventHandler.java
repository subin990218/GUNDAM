package com.mobilesuit.clientplugin.event.handler;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.TabLabel;
import com.intellij.util.ui.UIUtil;
import com.mobilesuit.clientplugin.client.SocketClient;
import com.mobilesuit.clientplugin.renderer.CirclePanel;
import com.mobilesuit.clientplugin.renderer.LivePanel;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.util.MessageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EventHandler {
    private static final EventHandler instance = new EventHandler();

    private static final DataContainer dataContainer = DataContainer.getInstance();
    //private final SocketClient socketClient = SocketClient.getInstance();

    private EventHandler() {
    }

    public static EventHandler getInstance() {
        return instance;
    }

    public void panelAlign() {
        if (!dataContainer.getStarter()) return;
        //ApplicationManager.getApplication().invokeLater(() -> {
        UIUtil.invokeLaterIfNeeded(() -> {
            FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(dataContainer.getProject());
            //EditorWindow editorWindow = fileEditorManager.getCurrentWindow();
            //EditorWindow editorWindow = fileEditorManager.getWindows()[0];
            EditorWindow[] editorWindows = fileEditorManager.getWindows();
            System.out.println("윈도우 갯수 : " + editorWindows.length);

            WindowManager windowManager = WindowManager.getInstance();
            JFrame intelliJFrame = windowManager.getFrame(dataContainer.getProject());

            System.out.println(intelliJFrame.getLocationOnScreen());

            Point screenPosition = intelliJFrame.getLocationOnScreen();
            if (screenPosition.y < 0) screenPosition.y = 0;

            dataContainer.getLivePanel().removeAll();
            dataContainer.getLivePanel().revalidate();
            dataContainer.getLivePanel().repaint();

            System.out.println("Components 갯수 : " + dataContainer.getLivePanel().getComponents().length);


            int i = 0;
            for (EditorWindow editorWindowGet : editorWindows) {
                JBTabs tempTab = editorWindowGet.getTabbedPane().getTabs();
                Point editorTabPosition = editorWindowGet.getTabbedPane().getComponent().getLocationOnScreen();


                System.out.println("editorwindow[" + editorWindowGet.getTabbedPane().getComponent().getWidth() + "] :" + editorTabPosition);
                for (TabInfo info : tempTab.getTabs()) {
                    try {
                        Point tabLocationOnScreen = tempTab.getTabLabel(info).getLocationOnScreen();
                        System.out.println("=========" + info);
                        System.out.println(tabLocationOnScreen);
                        String infoPath = info.getTooltipText();
                        if (null == infoPath) infoPath = dataContainer.getLastFile();
                        String tabPath = infoPath.replace("\\", "/").replace(dataContainer.getProjectPath(), "");

                        System.out.println("[" + tabLocationOnScreen + "]" + info + " : " + info.getTooltipText());
                        if (editorTabPosition.x <= tabLocationOnScreen.x) {
                            if (dataContainer.getFileMap().containsKey(tabPath)) {
                                AtomicInteger pixel = new AtomicInteger();
                                for (String userName : dataContainer.getFileMap().get(tabPath)) {
                                    SwingUtilities.invokeLater(() -> {
                                        CirclePanel circlePanel = new CirclePanel(
                                                tabLocationOnScreen.x - screenPosition.x + pixel.get(),
                                                tabLocationOnScreen.y - screenPosition.y,
                                                userName,
                                                dataContainer.getColorMap().get(userName));
                                        circlePanel.setToolTipText(userName);

                                        JPopupMenu popupMenu = new JPopupMenu();
                                        JMenuItem menuItem = new JMenuItem("Msg To " + userName);
                                        popupMenu.add(menuItem);

                                        menuItem.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                ApplicationManager.getApplication().invokeLater(() -> {
                                                    String message = Messages.showInputDialog("Please enter the message."
                                                            , "Message to " + userName, Messages.getQuestionIcon());
                                                    // 사용자 입력 처리 로직
                                                    if (null != message && message.length() > 0) {
                                                        MessageUtil.sendMessage(userName, message);
                                                    }
                                                });
                                            }
                                        });

                                        circlePanel.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseReleased(MouseEvent e) {
                                                if (SwingUtilities.isRightMouseButton(e)) {
                                                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                                                }
                                            }
                                        });

                                        dataContainer.getLivePanel().add(circlePanel);
                                        dataContainer.getLivePanel().repaint();
                                        pixel.addAndGet(12);
                                    });
                                }
                            }
                        }
                        i++;

                    } catch (IllegalComponentStateException e) {

                    }
                }

            }

            LivePanel livePanel = dataContainer.getLivePanel();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            livePanel.setBounds(0, 0, screenSize.width, screenSize.height);
            livePanel.repaint();
            dataContainer.getOverlayFrame().repaint();
        });
    }

    private static class Tabs {
        int width;
        int x;
        int y;
        TabLabel tabLabel;
    }
}
