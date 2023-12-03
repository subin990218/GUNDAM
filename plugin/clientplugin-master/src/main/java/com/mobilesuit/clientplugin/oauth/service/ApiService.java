package com.mobilesuit.clientplugin.oauth.service;

import com.google.gson.Gson;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.mobilesuit.clientplugin.client.SocketClient;
import com.mobilesuit.clientplugin.event.service.FileChangeDetector;
import com.mobilesuit.clientplugin.oauth.dto.GitHubUserDto;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.util.MyRestClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {
    private static final ApiService instance = new ApiService();

    private final DataContainer dataContainer = DataContainer.getInstance();
    private final SocketClient socketClient = SocketClient.getInstance();

    private ApiService() {

    }

    public static ApiService getInstance() {
        return instance;
    }

    public void checkAuthority() {
        //new Thread(() -> {
        String url = "https://api.github.com/repos" + dataContainer.getEndPath().replace(".git", "");
        System.out.println("요청:" + url);

        String jsonPayload = "";

        Map<String, String> header = new HashMap<>();

        header.put("Authorization", "Bearer " + dataContainer.getGitHubAccessToken());

        Headers headers = Headers.of(header);
        MyRestClient.getAsync(url, headers, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 정상 응답 시 콜백 함수
                String responseBody = response.body().string();

                System.out.println(responseBody);
                Gson gson = new Gson();
                Map responseMap = gson.fromJson(responseBody, Map.class);

                String endpoint = ((String) responseMap.get("collaborators_url")).replace("{/collaborator}", "") + "/" + dataContainer.getUserId() + "/permission";
                System.out.println(endpoint);

                MyRestClient.getAsync(endpoint, headers, new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String responseBody = response.body().string();
                            System.out.println(responseBody);


                            Map permissionMap = gson.fromJson(responseBody, Map.class);

                            String permission = (String) permissionMap.get("permission");
                            System.out.println(permission);
                            if (permission.equals("write") || permission.equals("admin") || permission.equals("maintain")) {
                                socketClient.connect();
                                notice("Logon", "Hello, "+dataContainer.getUserId()+"!", NotificationType.INFORMATION);
                            } else {
                                notice("Error", "You don't have permission for this repository.", NotificationType.ERROR);
                            }
                        }catch (NullPointerException e){
                            notice("Error", "You don't have permission for this repository.", NotificationType.ERROR);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        notice("Error", "Repository Permission Error", NotificationType.ERROR);
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notice("Error", "Check Repository Error", NotificationType.ERROR);
                e.printStackTrace();
            }
        });
        //}).start();
    }

    private void notice(String title, String content, NotificationType type) {
        Notification notification = new Notification(FileChangeDetector.NOTIFICATION_GROUP_ID, title, content, type);

            /*
            notification.addAction(new NotificationAction("Commit") {
                @Override
                public void actionPerformed(AnActionEvent e, Notification notification) {
                    notification.expire();
                }
            });
            notification.addAction(new NotificationAction("Next") {
                @Override
                public void actionPerformed(AnActionEvent e, Notification notification) {
                    notification.expire();
                }
            });*/
        Notifications.Bus.notify(notification, dataContainer.getProject());
        System.out.println(dataContainer.getProject()+" notice");

    }

    public void setUser() {
        String url = "https://api.github.com/user";

        String jsonPayload = "";

        Map<String, String> header = new HashMap<>();

        header.put("Authorization", "Bearer " + dataContainer.getGitHubAccessToken());

        Headers headers = Headers.of(header);
        MyRestClient.getAsync(url, headers, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 정상 응답 시 콜백 함수
                String responseBody = response.body().string();

                Gson gson = new Gson();

                GitHubUserDto userDto = gson.fromJson(responseBody, GitHubUserDto.class);

                dataContainer.setUserId(userDto.getLogin());
                dataContainer.setUserAvatarUrl(userDto.getAvatar_url());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }
}
