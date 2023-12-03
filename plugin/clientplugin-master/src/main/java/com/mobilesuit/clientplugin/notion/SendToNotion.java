package com.mobilesuit.clientplugin.notion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.mobilesuit.clientplugin.setting.GeneralSettingsState;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendToNotion extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();

        VirtualFile selectedFile = FileEditorManager.getInstance(project).getSelectedFiles()[0];

        String absolutePath = selectedFile.getPath();

        GeneralSettingsState settingsState = GeneralSettingsState.getInstance(project);

        String notionToken = settingsState.getNotionAPIKey();
        String databaseId = settingsState.getNotionDatabaseID();

        if (notionToken != null && databaseId != null) {
            String markdownContent = readMarkdownFile(absolutePath);
            createNotionPage(notionToken, databaseId, markdownContent);
        }
    }

    private static void createNotionPage(String notionToken, String databaseId, String markdownContent) {
        Gson gson = new Gson();

        JsonObject parentObject = new JsonObject();
        parentObject.addProperty("database_id", databaseId);

        JsonObject textObject = new JsonObject();
        textObject.addProperty("content", markdownContent);

        JsonArray richTextArray = new JsonArray();
        JsonObject richTextObject = new JsonObject();
        richTextObject.add("text", textObject);
        richTextArray.add(richTextObject);

        JsonObject textBlockObject = new JsonObject();
        textBlockObject.addProperty("object", "block");
        textBlockObject.addProperty("type", "paragraph");
        textBlockObject.add("paragraph", new JsonObject());
        textBlockObject.getAsJsonObject("paragraph").add("rich_text", richTextArray);

        JsonArray childrenArray = new JsonArray();
        childrenArray.add(textBlockObject);

        String currentTime = getCurrentTime();
        String pageTitle = "문서화 결과 - " + currentTime;

        JsonObject titleObject = new JsonObject();
        JsonObject titleTextObject = new JsonObject();
        titleTextObject.addProperty("content", pageTitle);
        titleObject.add("text", titleTextObject);

        JsonArray titleArray = new JsonArray();
        titleArray.add(titleObject);

        JsonObject propertiesObject = new JsonObject();
        propertiesObject.add("title", titleArray);

        JsonObject requestDataObject = new JsonObject();
        requestDataObject.add("parent", parentObject);
        requestDataObject.add("properties", propertiesObject);
        requestDataObject.add("children", childrenArray);

        String requestData = gson.toJson(requestDataObject);

        HttpClient httpClient = HttpClients.createDefault();

        HttpPost request = new HttpPost("https://api.notion.com/v1/pages");
        request.setHeader("Authorization", "Bearer " + notionToken);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Notion-Version", "2022-06-28");

        StringEntity params = new StringEntity(requestData, StandardCharsets.UTF_8);
        request.setEntity(params);

        try {
            HttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                Messages.showInfoMessage("페이지가 성공적으로 생성되었습니다!", "성공!");
            } else {
                System.out.println("페이지 생성에 실패했습니다. 응답 코드: " + statusCode);
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.println("응답 본문: " + responseBody);
                if (statusCode == 400) {
                    Messages.showErrorDialog("잘못된 데이터 베이스 입력입니다!", "에러!");
                }
                if (statusCode == 401) {
                    Messages.showErrorDialog("잘못된 토큰 입력입니다!", "에러!");
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static String readMarkdownFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
