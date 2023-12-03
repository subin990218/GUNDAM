package com.mobilesuit.clientplugin.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.dvcs.push.PushSource;
import com.intellij.dvcs.push.PushSpec;
import com.intellij.dvcs.push.PushTarget;
import com.intellij.dvcs.repo.Repository;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorGutter;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.mobilesuit.clientplugin.event.handler.EventHandler;
import com.mobilesuit.clientplugin.event.service.FileChangeDetector;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.websocket.dto.SessionInfo;
import com.mobilesuit.clientplugin.websocket.dto.WebSocketDto;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public final class SocketClient {


    private static final SocketClient instance = new SocketClient();

    private final DataContainer dataContainer = DataContainer.getInstance();
    private final EventHandler eventHandler = EventHandler.getInstance();

    private URI uri;

    private SocketClient() {
    }

    public static SocketClient getInstance() {
        return instance;
    }


    private WebSocketClient webSocketClient;

    private void makeClient() {
        uri = URI.create("ws://k9e207a.p.ssafy.io:8090/socket" + dataContainer.getEndPath());
        //uri = URI.create("ws://localhost:8090/socket" + dataContainer.getEndPath());

        System.out.println(uri);
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                log.info("Socket Connection State : " + String.valueOf(webSocketClient.getReadyState()));
                log.info("[" + handshakedata.getHttpStatus() + "] : " + handshakedata.getHttpStatusMessage());
                Gson gson = new Gson();

                String userName = dataContainer.getUserId();

                /*WebSocketDto.UserInfo userInfo = WebSocketDto.UserInfo.builder()
                        .userName(userName)
                        .code(0)
                        .target("token")
                        .build();

                String userInfoString = gson.toJson(userInfo);*/

                WebSocketDto.Event event = WebSocketDto.Event.builder()
                        .code("OPEN")
                        .text(userName)
                        .build();

                String eventString = gson.toJson(event);

                webSocketClient.send(eventString);

            /*webSocketClient.send("{\n" +
                    "    \"code\":\"OPEN\",\n" +
                    "    \"text\":\"token\"\n" +
                    "}");*/
            }

            @Override
            public void onMessage(String message) {
                Gson gson = new Gson();
                System.out.println("Message Received : " + message);

                WebSocketDto.Event event = gson.fromJson(message, WebSocketDto.Event.class);

                if (event.getCode().equals("USER")) {
                    WebSocketDto.UserInfo userInfo = gson.fromJson(event.getText(), WebSocketDto.UserInfo.class);

                    switch (userInfo.getCode()) {
                        case 0:
                            dataContainer.addColorMap(userInfo.getUserName());
                            break;
                        case 1: // 파일 이동
                            dataContainer.moveToFile(userInfo.getTarget(), userInfo.getUserName());
                            break;
                        case 2: // 상태 변화
                            dataContainer.getUserStatus().put(userInfo.getUserName(), userInfo.getTarget());
                            break;
                        default:
                            break;
                    }
                    System.out.println();
                    for (String userName : dataContainer.getUserList().keySet()) {
                        System.out.println(userName + "[" + dataContainer.getUserStatus().get(userName) + "] : " + dataContainer.getUserList().get(userName));
                    }
                    System.out.println();

                    redraw();
                } else if (event.getCode().equals("OPEN")) {
                    initWhenOpen();
                    dataContainer.addColorMap(event.getText());

                    redraw();
                } else if (event.getCode().equals("PING")) {
                    handlePing(event.getText());
                } else if (event.getCode().equals("STATUS")) {
                    System.out.println(event.getText());
                    List<SessionInfo> infoList = gson.fromJson(event.getText(), new TypeToken<ArrayList<SessionInfo>>() {
                    }.getType());
                    for (SessionInfo sessionInfo : infoList) {
                        dataContainer.addColorMap(sessionInfo.getUserName());
                        if (null == sessionInfo.getOnFile() || null == sessionInfo.getUserName()) continue;
                        dataContainer.moveToFile(sessionInfo.getOnFile(), sessionInfo.getUserName());
                    }
                    redraw();
                } else if (event.getCode().equals("CLOSE")) {
                    String userName = event.getText();
                    dataContainer.logOutUser(userName);
                    redraw();
                }else if(event.getCode().equals("PUSH")){
                    WebSocketDto.UserInfo info = gson.fromJson(event.getText(), WebSocketDto.UserInfo.class);
                    handlePushEvent(info);
                }
                else if(event.getCode().equals("MSG")){
                    WebSocketDto.UserInfo receivedMessage = gson.fromJson(event.getText(), WebSocketDto.UserInfo.class);

                    String from = receivedMessage.getUserName().split(":")[0];
                    String to = receivedMessage.getUserName().split(":")[1];

                    String onMessage = receivedMessage.getTarget();

                    StringBuilder sb = new StringBuilder();

                    DateTimeFormatter dateTimeFormatter =DateTimeFormatter.ofPattern("HH:mm:ss");


                    sb.append("[").append(LocalDateTime.now().format(dateTimeFormatter)).append("]\n").append(from).append(">> ").append(onMessage);

                    dataContainer.getPluginPanel().appendText(sb.toString());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                webSocketClient.send("closing");
                this.close();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    private void redraw() {
        SwingUtilities.invokeLater(() -> {
            eventHandler.panelAlign();
        });
    }

    public void connect() {
        makeClient();
        webSocketClient.addHeader("authorization", "token");
        try {
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            if (dataContainer.getStarter()) {
                webSocketClient.send(message);
            }
        } catch (WebsocketNotConnectedException e) {
            log.error("Socket Not Connected");
        }
    }

    public void sendPing(char dir) {
        if (null == webSocketClient || webSocketClient.isClosed()) return;
        Gson gson = new Gson();
        Editor editor = FileEditorManager.getInstance(dataContainer.getProject()).getSelectedTextEditor();
        CaretModel caretModel = editor.getCaretModel();
        LogicalPosition logicalPosition = caretModel.getLogicalPosition();
        int currentLine = logicalPosition.line;
        int currentColumn = logicalPosition.column;

        System.out.println(dataContainer.getLastFile());
        System.out.println(currentLine + " " + currentColumn);
        WebSocketDto.UserInfo userInfo = WebSocketDto.UserInfo.builder()
                .userName(dataContainer.getUserId())
                .code(dir)
                .target(dataContainer.getLastFile() + ":" + currentLine + ":" + currentColumn)
                .build();

        String userInfoString = gson.toJson(userInfo);

        WebSocketDto.Event event = WebSocketDto.Event.builder()
                .code("PING")
                .text(userInfoString)
                .build();

        send(gson.toJson(event));

        System.out.println(dir + "send Ping");
        if (dir == 'L') dataContainer.getAlertPanel().myPingDraw(dataContainer.getStartPoint(), "mia");
        if (dir == 'U') dataContainer.getAlertPanel().myPingDraw(dataContainer.getStartPoint(), "kiken");
        if (dir == 'D') dataContainer.getAlertPanel().myPingDraw(dataContainer.getStartPoint(), "help");
        if (dir == 'R') dataContainer.getAlertPanel().myPingDraw(dataContainer.getStartPoint(), "going");
    }

    public void sendMessage(String userName, String message) {
        Gson gson = new Gson();
        WebSocketDto.UserInfo userInfo = WebSocketDto.UserInfo.builder()
                .userName(dataContainer.getUserId()+":"+userName).code(1).target(message).build();
        String text = gson.toJson(userInfo);

        WebSocketDto.Event event = WebSocketDto.Event.builder()
                .code("MSG").text(text).build();

        webSocketClient.send(gson.toJson(event));
    }

    public void sendPushEvent(PushSpec<PushSource, PushTarget> pushSpec, Repository repository, List<VcsFullCommitDetails> commitDetails){
        Gson gson = new Gson();

        for(VcsFullCommitDetails vcsFullCommitDetails : commitDetails){
            System.out.println(vcsFullCommitDetails.getFullMessage());
            ;
        }

        WebSocketDto.UserInfo userInfo = WebSocketDto.UserInfo.builder()
                .userName(dataContainer.getUserId()).code(1).target(pushSpec.toString()).build();

        String text = gson.toJson(userInfo);

        WebSocketDto.Event event = WebSocketDto.Event.builder()
                .code("PUSH").text(text).build();

        webSocketClient.send(gson.toJson(event));
    }

    private void handlePushEvent(WebSocketDto.UserInfo userInfo){
        if(userInfo.getUserName().equals(dataContainer.getUserId())) return;
        System.out.println(userInfo.getUserName());
        System.out.println(userInfo.getTarget());
        notice(userInfo.getUserName()+" Git Pushed",userInfo.getTarget(),NotificationType.INFORMATION);
    }


    private void initWhenOpen() {
        SwingUtilities.invokeLater(() -> {
            FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(dataContainer.getProject());
            EditorWindow[] editorWindows = fileEditorManager.getWindows();
            String currentPath = null;

            if (null == editorWindows || editorWindows.length == 0) return;
            for (EditorWindow editorWindowGet : editorWindows) {
                if (null != editorWindowGet && null != editorWindowGet.getSelectedFile()) {
                    currentPath = editorWindowGet.getSelectedFile().getCanonicalPath();
                    break;
                }
            }
            if (null == currentPath) return;

            String projectPath = dataContainer.getProjectPath();
            String path = currentPath.replace(projectPath, "");
            dataContainer.setLastFile(path);

            //String pathBackSlash = projectPath.replace("/","\\");

            dataContainer.setProjectPathBackSlash(projectPath);

            Gson gson = new Gson();

            String userName = dataContainer.getUserId();

            String userInfo = gson.toJson(WebSocketDto.UserInfo.builder().code(1).userName(userName).target(path));

            String data = gson.toJson(WebSocketDto.Event.builder().code("USER").text(userInfo));
            send(data);

            eventHandler.panelAlign();
        });
    }

    private void handlePing(String eventText) {
        Gson gson = new Gson();
        System.out.println(eventText);
        WebSocketDto.UserInfo userInfo = gson.fromJson(eventText, WebSocketDto.UserInfo.class);

        String userName = userInfo.getUserName();

        if (userName.equals(dataContainer.getUserId())) return;

        char dir = (char) userInfo.getCode();

        String targetFile = userInfo.getTarget().split(":")[0];

        int lineNumber = Integer.parseInt(userInfo.getTarget().split(":")[1]);
        int columnNumber = Integer.parseInt(userInfo.getTarget().split(":")[2]);


        String pingName;
        switch (dir) {
            case 'U' -> pingName = "kiken";
            case 'L' -> pingName = "mia";
            case 'R' -> pingName = "going";
            default -> pingName = "help";
        }
        StringBuilder sb = new StringBuilder();

        DateTimeFormatter dateTimeFormatter =DateTimeFormatter.ofPattern("HH:mm:ss");


        sb.append("[").append(LocalDateTime.now().format(dateTimeFormatter)).append("]\n").append(userName).append("'s ").append(pingName).append(" on ").append(targetFile).append(" in line ").append(lineNumber);

        dataContainer.getPluginPanel().appendText(sb.toString());

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(dataContainer.getProject());
        Editor editor = fileEditorManager.getSelectedTextEditor();
        //EditorGutter gutter = editor.getGutter();
        SwingUtilities.invokeLater(() -> {
            Point point = new Point(100, 100);
            if (null == editor || !targetFile.equals(dataContainer.getLastFile())) {
                System.out.println(targetFile);
                System.out.println(dataContainer.getLastFile());
            } else {

                /*System.out.println(getProjectViewSize(dataContainer.getProject()));

                System.out.println(editor.getComponent().getLocation());
                System.out.println(editor.getComponent().getLocationOnScreen());


                System.out.println(editor.getScrollingModel().getVisibleArea());

                System.out.println("line" + Arrays.toString(editor.visualLineToYRange(1)));
                System.out.println(editor.visualLineToY(1));

                System.out.println("line" + Arrays.toString(editor.visualLineToYRange(23)));
                System.out.println(editor.visualLineToY(23));*/

                Rectangle visibleArea = editor.getScrollingModel().getVisibleArea();


                System.out.println("on line : " + editor.visualLineToY(lineNumber));
                if (visibleArea.y <= editor.visualLineToY(lineNumber)
                        && editor.visualLineToY(lineNumber) <= visibleArea.height + visibleArea.y) {
                    System.out.println("보인다");
                    point = new Point(getProjectViewSize(dataContainer.getProject()).width
                            , editor.visualLineToY(lineNumber) - visibleArea.y);
                    System.out.println(point);
                }

            }
            dataContainer.getAlertPanel().otherPingDraw(point, pingName, userName);
            dataContainer.getOverlayFrame().repaint();
        });
    }

    /*public void closeSession(){
        webSocketClient.close(1000,"end IDE");
    }*/

    private void notice(String title, String content, NotificationType type) {
        Notification notification = new Notification(FileChangeDetector.NOTIFICATION_GROUP_ID, title, content, type);
        Notifications.Bus.notify(notification, dataContainer.getProject());
    }

    private Dimension getProjectViewSize(Project project) {
        ProjectViewImpl projectView = (ProjectViewImpl) ProjectViewImpl.getInstance(project);

        return projectView.getComponent().getSize();
    }
}
