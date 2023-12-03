package com.mobilesuit.clientplugin.appservice;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.JBColor;
import com.mobilesuit.clientplugin.client.SocketClient;
import com.mobilesuit.clientplugin.event.handler.EventHandler;
import com.mobilesuit.clientplugin.event.service.FileChangeDetector;
import com.mobilesuit.clientplugin.form.PluginOption;
import com.mobilesuit.clientplugin.listener.MyFileEditorManagerListener;
import com.mobilesuit.clientplugin.oauth.service.ApiService;
import com.mobilesuit.clientplugin.ping.PingCalc;
import com.mobilesuit.clientplugin.renderer.AlertPanel;
import com.mobilesuit.clientplugin.renderer.LivePanel;
import com.mobilesuit.clientplugin.renderer.PingPanel;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.util.SecureUtil;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class PluginBackGroundTask extends Task.Backgroundable {

    private final DataContainer dataContainer = DataContainer.getInstance();
    private final EventHandler eventHandler = EventHandler.getInstance();
    private final ApiService apiService = ApiService.getInstance();
    private final PluginService pluginService = ApplicationManager.getApplication().getService(PluginService.class);

    private PluginOption pluginOption;
    private Project project;

    public PluginBackGroundTask(Project project, PluginOption pluginOption) {
        super(project, "Background task");
        this.pluginOption = pluginOption;
        this.project = project;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        SocketClient socketClient = SocketClient.getInstance();

        ApplicationManager.getApplication().invokeLater(() -> {

            try {
                if (!dataContainer.getStarter() && null != project) {

                    String basePath = project.getBasePath();

                    FileRepositoryBuilder builder = new FileRepositoryBuilder();
                    Repository repository = builder.setGitDir(new File(basePath + "/.git"))
                            .readEnvironment() // scan environment GIT_* variables
                            .findGitDir() // scan up the file system tree
                            .build();
                    StoredConfig config = repository.getConfig();
                    String url = config.getString("remote", "origin", "url");
                    dataContainer.setEndPath(url.replace("https://github.com", ""));


                    dataContainer.setStarter(true);
                    String token = SecureUtil.getToken();
                    if (null != token) {
                        System.out.println(token);
                        dataContainer.setGitHubAccessToken(token);
                        apiService.checkAuthority();
                        apiService.setUser();
                    } else
                        BrowserUtil.browse("http://k9e207.p.ssafy.io:7070" + "/oauth2/authorization/github?device=plugin");


                    dataContainer.setPluginPanel(pluginOption.getPluginPanel());
                    pluginOption.remove();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (NullPointerException enx) {
                pluginService.getHasRun().set(false);
                Notification notification = new Notification(FileChangeDetector.NOTIFICATION_GROUP_ID, "Repository error", "Cannot Find .git folder linked with this project.", NotificationType.ERROR);
                Notifications.Bus.notify(notification, project);
                return;
            }

            WindowManager windowManager = WindowManager.getInstance();
            IdeFrame ideFrame = windowManager.getIdeFrame(this.getProject());

            JFrame intelliJFrame = windowManager.getFrame(this.getProject());

            intelliJFrame.addWindowFocusListener(new WindowAdapter() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    // IntelliJ 창이 포커스를 얻었을 때 수행할 작업
                    System.out.println("IntelliJ window gained focus");
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    // IntelliJ 창이 포커스를 잃었을 때 수행할 작업
                    System.out.println("IntelliJ window lost focus");
                }
            });

            dataContainer.setMainFrame(intelliJFrame);

            final JDialog overlayFrame = new JDialog(intelliJFrame);
            overlayFrame.setSize(intelliJFrame.getSize());
            overlayFrame.setLocation(intelliJFrame.getLocation());

            // 오버레이 프레임이 아무런 장식도 없게합니다.
            overlayFrame.setUndecorated(true);

            // 오버레이 프레임이 투명 배경이 됩니다.
            overlayFrame.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))); // Last parameter is Alpha

            dataContainer.setOverlayFrame(overlayFrame);

            Timer resizeTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("OverlayFrame Resized On :" + overlayFrame.getSize());
                    System.out.println("OverlayFrame Moved On :" + overlayFrame.getLocation());
                    SwingUtilities.invokeLater(() -> {
                        eventHandler.panelAlign();
                    });
                }
            });
            resizeTimer.setRepeats(false);

            Timer editorTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        eventHandler.panelAlign();
                    });
                }
            });
            editorTimer.setRepeats(false);
            dataContainer.setEditorTimer(editorTimer);

            intelliJFrame.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    dataContainer.getOverlayFrame().setSize(e.getComponent().getSize());
                    dataContainer.getOverlayFrame().setLocation(e.getComponent().getLocation());
                    resizeTimer.restart();
                }

                public void componentMoved(ComponentEvent e) {
                    dataContainer.getOverlayFrame().setSize(e.getComponent().getSize());
                    dataContainer.getOverlayFrame().setLocation(e.getComponent().getLocation());
                    resizeTimer.restart();
                }

                public void componentShown(ComponentEvent e) {
                    overlayFrame.setVisible(true);
                }

                public void componentHidden(ComponentEvent e) {
                    overlayFrame.setVisible(false);
                }
            });

            FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(dataContainer.getProject());

            EditorWindow editorWindow = fileEditorManager.getCurrentWindow();

            if (null != editorWindow) {
                editorWindow.getTabbedPane().getComponent().addComponentListener(
                        new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                editorTimer.restart();
                            }
                        });
                ComponentManager componentManager = getProject().getActualComponentManager();
                final MyFileEditorManagerListener myFileEditorManagerListener = componentManager.getService(MyFileEditorManagerListener.class);

                myFileEditorManagerListener.setRegistered(true);
            }

            overlayFrame.setVisible(true);


            LivePanel livePanel = new LivePanel();
            livePanel.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))); // Last parameter is Alpha
            livePanel.setLayout(null);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            livePanel.setBounds(0, 0, screenSize.width, screenSize.height);
            dataContainer.getOverlayFrame().getContentPane().add(livePanel);

            livePanel.setVisible(true);
            dataContainer.setLivePanel(livePanel);
            livePanel.repaint();


            LivePanel pingOverlayPanel = new LivePanel();
            pingOverlayPanel.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))); // Last parameter is Alpha
            pingOverlayPanel.setLayout(null);
            pingOverlayPanel.setBounds(0, 0, screenSize.width, screenSize.height);
            pingOverlayPanel.setVisible(true);
            pingOverlayPanel.repaint();
            dataContainer.getOverlayFrame().getContentPane().add(pingOverlayPanel);

            PingPanel pingPanel = new PingPanel();

            pingOverlayPanel.add(pingPanel);
            pingPanel.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))); // Last parameter is Alpha
            pingPanel.setVisible(false);
            pingPanel.setBounds(0, 0, 272, 272);

            livePanel.repaint();

            overlayFrame.setLayout(new OverlayLayout(overlayFrame.getContentPane()));

            AlertPanel alertPanel = new AlertPanel();

            overlayFrame.add(alertPanel);
            alertPanel.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))); // Last parameter is Alpha
            alertPanel.setVisible(true);
            alertPanel.setBounds(0, 0, 384, 500);

            /*alertPanel.pingShow();*/

            dataContainer.setAlertPanel(alertPanel);

            dataContainer.setCtrlPressed(false);
            dataContainer.setMousePressed(false);


            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                public void eventDispatched(AWTEvent e) {
                    if (e instanceof MouseEvent) {
                        MouseEvent me = (MouseEvent) e;
                        //if (dataContainer.getCtrlPressed() && me.getID() == MouseEvent.MOUSE_MOVED) {
                        if (me.getID() == MouseEvent.MOUSE_DRAGGED && SwingUtilities.isRightMouseButton(me) && me.isControlDown()) {
                            /*System.out.println("Button : "+SwingUtilities.isRightMouseButton(me));*/
                            if (!dataContainer.getMousePressed()) {
                                Point point = me.getLocationOnScreen();

                                dataContainer.setStartPoint(point);

                                Point point_ide = intelliJFrame.getLocationOnScreen();

                                pingPanel.setVisible(true);
                                pingPanel.setLocation(point.x - pingPanel.getWidth() / 2 - point_ide.x, point.y - pingPanel.getHeight() / 2 - point_ide.y);
                                pingPanel.repaint();
                                dataContainer.getOverlayFrame().repaint();
                                dataContainer.setMousePressed(true);
                            }
                            Point nextPoint = me.getLocationOnScreen();

                            char dir = PingCalc.getDir(dataContainer.getStartPoint(), nextPoint);


                            pingPanel.setDir(dir);
                            pingPanel.repaint();
                            dataContainer.getOverlayFrame().repaint();

                        }
                    }
                }
            }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                public void eventDispatched(AWTEvent e) {
                    if (e instanceof MouseEvent) {
                        MouseEvent me = (MouseEvent) e;
                        if (me.getID() == MouseEvent.MOUSE_RELEASED && SwingUtilities.isRightMouseButton(me) && me.isControlDown()) {

                            Point nextPoint = me.getLocationOnScreen();

                            char dir = PingCalc.getDir(dataContainer.getStartPoint(), nextPoint);

                            if (dir != 'X') {
                                socketClient.sendPing(dir);
                            }

                            pingPanel.setDir('X');
                            dataContainer.setCtrlPressed(false);
                            dataContainer.setMousePressed(false);
                            pingPanel.setVisible(false);
                            dataContainer.getOverlayFrame().repaint();
                        }
                    }
                }
            }, AWTEvent.MOUSE_EVENT_MASK);

            pingPanel.setFocusable(true);

            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                public void eventDispatched(AWTEvent e) {
                    if (e instanceof KeyEvent) {
                        KeyEvent ke = (KeyEvent) e;
                        if (ke.getID() == KeyEvent.KEY_PRESSED && ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                            dataContainer.setCtrlPressed(true);
                        } else if (ke.getID() == KeyEvent.KEY_RELEASED && ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                            pingPanel.setDir('X');
                            dataContainer.setCtrlPressed(false);
                            dataContainer.setMousePressed(false);
                            pingPanel.setVisible(false);
                            dataContainer.getOverlayFrame().repaint();
                        }
                    }
                }
            }, AWTEvent.KEY_EVENT_MASK);

            SwingUtilities.invokeLater(() -> {
                eventHandler.panelAlign();
            });
        });

        getProject().getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new MyFileEditorManagerListener());
    }

}
