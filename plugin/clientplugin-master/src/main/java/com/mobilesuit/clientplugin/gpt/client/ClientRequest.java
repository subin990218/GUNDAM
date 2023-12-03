package com.mobilesuit.clientplugin.gpt.client;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mobilesuit.clientplugin.gpt.dto.CompletionChatResponse;
import com.mobilesuit.clientplugin.gpt.dto.TokenRequest;
import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
import com.mobilesuit.clientplugin.util.MyRestClient;
import com.mobilesuit.clientplugin.window.GPTResultWindowFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ClientRequest {

    public void requestMessage(Map<String,String> checkedSourceCodeMap, Project project,  JButton extractButton){
        // 이전 코드 리뷰 정보 삭제
        ResponseRepository repo = ResponseRepository.getInstance();

        //checkedFileSourceCodeList 의 내용을 spring에 전달
        Gson gson = new Gson();

        // checkedSourceCodeList가 비어있는지 확인
        if (checkedSourceCodeMap.isEmpty()) {
            // 여기에 원하는 예외 처리 또는 사용자에게 알림 제공 코드 추가
            JOptionPane.showMessageDialog(null, "선택된 코드가 없습니다.");  // 예: 메시지 대화상자로 알림
            extractButton.setIcon(null);
            extractButton.setText("Review Request");
            return;  // 더 이상 진행하지 않고 함수 종료
        }

        SwingUtilities.invokeLater(() -> {
            // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
            String toolWindowId = "GPTResult";
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
            GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();

            Result.initLoadingToolWindowContent(toolWindow);
        });

        // checkedFileSourceCodeList의 각 요소에 대해 요청을 보냅니다.
        for (Map.Entry<String, String> entry : checkedSourceCodeMap.entrySet()) {

            // List<String>을 JSON으로 변환합니다.
            String jsonPayload = gson.toJson(entry.getValue()); // 단일 sourceCode를 JSON으로 변환

            new Thread(() -> {
                String entryKey = entry.getKey();
                String url = "https://k9e207.p.ssafy.io/api/chatgpt/rest/completion/chat/prompt/create";
                MyRestClient.postAsync(url, jsonPayload, new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        // 정상 응답 시 콜백 함수
                        String responseBody = response.body().string();

                        //!!!! nginx에서 gateway time out 문제를 html 코드로 보내는 경우가 있다. 무조건 에러 뜨니 대비할것 !!!
                        System.out.println("response: " +responseBody);

                        Gson gson = new Gson();
                        CompletionChatResponse completionChatResponse = gson.fromJson(responseBody, CompletionChatResponse.class);

                        List<String> messages = completionChatResponse.getMessages().stream()
                                .map(CompletionChatResponse.Message::getMessage)
                                .collect(Collectors.toList());

                        //답변을 파싱해서 나눠서 저장
                        String category = messages.toString();
                        categoryData(entryKey,category);

                        // 여기서 하나씩 로딩 (타임아웃이 없다고 가정)
                        int allResSize = repo.getCodeInfoList().size(); // 모든 응답이 오면 창을 보여주기위해
                        int Id = repo.getCodeCommentsMap().size()-1; //Comment를 필수값이므로 현재 응답 번호 0,1,...

                        //String resCodeName = repo.getCodeInfoList().get(Id).getName(); // 이 응답이 순서대로 들어올 것이므로, Id번째가, 현재 응답의 코드 이름
                        String resCodeName = entryKey;
                        System.out.println();
                        System.out.println("response ID : " + Id + " response Name : " + resCodeName);
                        System.out.println("getCodeReviewMap : " + repo.getCodeReviewMap().size());
                        System.out.println("getCommitMessageMap : " + repo.getCommitMessageMap().size());

                        System.out.println("getCleanCodeMap : " + repo.getCleanCodeMap().size());
                        System.out.println("getCodeCommentsMap : " + repo.getCodeCommentsMap().size());
                        System.out.println("getCodeInfoMap : " + repo.getCodeInfoMap().size());
                        System.out.println("getCodeInfoList : " + repo.getCodeInfoList().size());

                        System.out.println("map data : " + repo.getCleanCodeMap().get(resCodeName));
                        SwingUtilities.invokeLater(() -> {
                            // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
                            String toolWindowId = "GPTResult";
                            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
                            GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();

                            Result.loadingToolWindowContent(resCodeName,toolWindow); //최근 응답을 넣어준다.

                            if(Id == allResSize-1 ){ // 모든 응답이 들어오면 창을 열어 알려준다.
                                System.out.println("Response End");
                                toolWindow.show();
                            }
                            // 모두 다 도착했을때 호출하도록 변경해야 한다.
                            extractButton.setIcon(null);
                            extractButton.setText("Review Request");
                        });
                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();

                        SwingUtilities.invokeLater(() -> {
                            extractButton.setIcon(null);
                            extractButton.setText("Review Request");
                        });
                        categoryData("데이터가 잘못 왔음", entryKey);
                    }
                });
            }).start();
        }
    }

    public void requestMessageToken(Map<String,String> checkedSourceCodeMap, Project project,
                                    JButton extractButton, String user, String token,String url, String model){

        // 이전 코드 리뷰 정보 삭제
        ResponseRepository repo = ResponseRepository.getInstance();

        //checkedFileSourceCodeList 의 내용을 spring에 전달
        Gson gson = new Gson();

        // checkedSourceCodeList가 비어있는지 확인
        if (checkedSourceCodeMap.isEmpty()) {
            // 여기에 원하는 예외 처리 또는 사용자에게 알림 제공 코드 추가
            JOptionPane.showMessageDialog(null, "선택된 코드가 없습니다.");  // 예: 메시지 대화상자로 알림
            extractButton.setIcon(null);
            extractButton.setText("Review Request");
            return;  // 더 이상 진행하지 않고 함수 종료
        }

        SwingUtilities.invokeLater(() -> {
            // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
            String toolWindowId = "GPTResult";
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
            GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();

            Result.initLoadingToolWindowContent(toolWindow);
        });

        // checkedFileSourceCodeList의 각 요소에 대해 요청을 보냅니다.
        for (Map.Entry<String, String> entry : checkedSourceCodeMap.entrySet()) {
            // List<String>을 JSON으로 변환합니다.
            TokenRequest tokenRequest = new TokenRequest(token,user,entry.getValue(),model);
            String jsonPayload = gson.toJson(tokenRequest);
            new Thread(() -> {
                String entryKey = entry.getKey();

                MyRestClient.postAsync(url, jsonPayload, new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        // 정상 응답 시 콜백 함수
                        String responseBody = response.body().string();

                        //!!!! nginx에서 gateway time out 문제를 html 코드로 보내는 경우가 있다. 무조건 에러 뜨니 대비할것 !!!
                        System.out.println("response: " +responseBody);

                        Gson gson = new Gson();
                        CompletionChatResponse completionChatResponse = gson.fromJson(responseBody, CompletionChatResponse.class);

                        List<String> messages = completionChatResponse.getMessages().stream()
                                .map(CompletionChatResponse.Message::getMessage)
                                .collect(Collectors.toList());

                        //답변을 파싱해서 나눠서 저장
                        String category = messages.toString();
                        categoryData2(entryKey,category);

                        // 여기서 하나씩 로딩 (타임아웃이 없다고 가정)
                        int allResSize = repo.getCodeInfoList().size(); // 모든 응답이 오면 창을 보여주기위해
                        int Id = repo.getCodeCommentsMap().size()-1; //Comment를 필수값이므로 현재 응답 번호 0,1,...

                        //String resCodeName = repo.getCodeInfoList().get(Id).getName(); // 이 응답이 순서대로 들어올 것이므로, Id번째가, 현재 응답의 코드 이름
                        String resCodeName = entryKey;
                        System.out.println();
                        System.out.println("response ID : " + Id + " response Name : " + resCodeName);
                        System.out.println("getCodeReviewMap : " + repo.getCodeReviewMap().size());
                        System.out.println("getCommitMessageMap : " + repo.getCommitMessageMap().size());

                        System.out.println("getCleanCodeMap : " + repo.getCleanCodeMap().size());
                        System.out.println("getCodeCommentsMap : " + repo.getCodeCommentsMap().size());
                        System.out.println("getCodeInfoMap : " + repo.getCodeInfoMap().size());
                        System.out.println("getCodeInfoList : " + repo.getCodeInfoList().size());

                        System.out.println("map data : " + repo.getCleanCodeMap().get(resCodeName));
                        SwingUtilities.invokeLater(() -> {
                            // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
                            String toolWindowId = "GPTResult";
                            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
                            GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();

                            Result.loadingToolWindowContent(resCodeName,toolWindow); //최근 응답을 넣어준다.

                            if(Id == allResSize-1 ){ // 모든 응답이 들어오면 창을 열어 알려준다.
                                System.out.println("Response End");
                                toolWindow.show();
                            }
                            // 모두 다 도착했을때 호출하도록 변경해야 한다.
                            extractButton.setIcon(null);
                            extractButton.setText("Review Request");
                        });
                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();

                        SwingUtilities.invokeLater(() -> {
                            extractButton.setIcon(null);
                            extractButton.setText("Review Request");
                        });
                        categoryData("데이터가 잘못 왔음", entryKey);
                    }
                });
            }).start();
        }
    }
    public void categoryData(String entryKey, String category) {
        ResponseRepository.getInstance().getResponseMap().put(entryKey, category);

        // 각 섹션의 타이틀
        String[] titles = new String[] {
                "코드 설명",
                "코드 리뷰",
                "클린 코드 규칙이 적용된 코드",
                "추천 커밋 메시지"
        };

        log.info("category: {}", category);
        // map에 저장하는 부분에서도 로그 기록


        category = category.replace("```java","");
        category = category.replace("```Java","");
        category = category.replace("```JAVA","");
        category = category.replace("```","");

        // 각 타이틀을 독립적으로 처리
        for (int i=0;i<titles.length;i++) {
            int startIndex = category.indexOf(titles[i]);

            if (startIndex != -1) {

                // 타이틀 뒤에 콜론(:)과 공백이 있는지 확인
                int titleEndIndex = startIndex + titles[i].length();
                int colonIndex = category.indexOf(":", titleEndIndex);
                if (colonIndex != -1) {
                    // 콜론 이전까지의 문자열이 공백으로만 이루어져 있는지 확인
                    String betweenTitleAndColon = category.substring(titleEndIndex, colonIndex).trim();
                    if (betweenTitleAndColon.isEmpty()) {
                        // 콜론 이후의 내용으로 시작 인덱스 조정
                        startIndex = colonIndex + 1;
                    }
                }
                // 다음 타이틀의 시작 인덱스를 찾거나, 없으면 문자열 끝까지
                int endIndex = -1;
                for (String nextTitle : titles) {
                    if (!nextTitle.equals(titles[i])) {
                        int tempIndex = category.indexOf(nextTitle, startIndex + titles[i].length());
                        if (tempIndex != -1 && (endIndex == -1 || tempIndex < endIndex)) {
                            endIndex = tempIndex;
                        }
                    }
                }

                if (endIndex == -1) {
                    endIndex = category.length();
                }

                String sectionContent = category.substring(startIndex, endIndex).trim();
                // 해당 타이틀에 대한 내용 저장
                ResponseRepository.getInstance().getListMap()[i].put(entryKey, sectionContent);
                log.info("Section content for {}: {}", titles[i], sectionContent);
            } else {
                // 데이터 전달 형식이 잘못된 경우
                ResponseRepository.getInstance().getListMap()[i].put(entryKey, "데이터 전달 방식 오류");
                log.info("Section content for {}: {}", titles[i], "데이터 전달 방식 오류");
            }
        }

        System.out.println("category:" + category + "\n");
    }


    public void categoryData2(String entryKey, String category) {
        ResponseRepository.getInstance().getResponseMap().put(entryKey, category);

        // 각 섹션의 타이틀
        String[] titles = new String[] {
                "코드 주석",
                "코드 리뷰",
                "클린 코드 규칙이 적용된 코드",
                "추천 커밋 메시지"
        };

        log.info("category: {}", category);
        // map에 저장하는 부분에서도 로그 기록


        category = category.replace("```java","");
        category = category.replace("```Java","");
        category = category.replace("```JAVA","");
        category = category.replace("```","");

        // 각 타이틀을 독립적으로 처리
        for (int i=0;i<titles.length;i++) {
            int startIndex = category.indexOf(titles[i]);

            if (startIndex != -1) {

                // 타이틀 뒤에 콜론(:)과 공백이 있는지 확인
                int titleEndIndex = startIndex + titles[i].length();
                int colonIndex = category.indexOf(":", titleEndIndex);
                if (colonIndex != -1) {
                    // 콜론 이전까지의 문자열이 공백으로만 이루어져 있는지 확인
                    String betweenTitleAndColon = category.substring(titleEndIndex, colonIndex).trim();
                    if (betweenTitleAndColon.isEmpty()) {
                        // 콜론 이후의 내용으로 시작 인덱스 조정
                        startIndex = colonIndex + 1;
                    }
                }
                // 다음 타이틀의 시작 인덱스를 찾거나, 없으면 문자열 끝까지
                int endIndex = -1;
                for (String nextTitle : titles) {
                    if (!nextTitle.equals(titles[i])) {
                        int tempIndex = category.indexOf(nextTitle, startIndex + titles[i].length());
                        if (tempIndex != -1 && (endIndex == -1 || tempIndex < endIndex)) {
                            endIndex = tempIndex;
                        }
                    }
                }

                if (endIndex == -1) {
                    endIndex = category.length();
                }

                String sectionContent = category.substring(startIndex, endIndex).trim();
                // 해당 타이틀에 대한 내용 저장
                ResponseRepository.getInstance().getListMap()[i].put(entryKey, sectionContent);
                log.info("Section content for {}: {}", titles[i], sectionContent);
            } else {
                // 데이터 전달 형식이 잘못된 경우
                ResponseRepository.getInstance().getListMap()[i].put(entryKey, "데이터 전달 방식 오류");
                log.info("Section content for {}: {}", titles[i], "데이터 전달 방식 오류");
            }
        }

    }// category2 end

    public void requestMessageTokenAsync(Map<String,String> checkedSourceCodeMap, Project project,
                                    JButton extractButton, String user, String token,String url, String model){

        // 이전 코드 리뷰 정보 삭제
        ResponseRepository repo = ResponseRepository.getInstance();

        //checkedFileSourceCodeList 의 내용을 spring에 전달
        Gson gson = new Gson();

        // checkedSourceCodeList가 비어있는지 확인
        if (checkedSourceCodeMap.isEmpty()) {
            // 여기에 원하는 예외 처리 또는 사용자에게 알림 제공 코드 추가
            JOptionPane.showMessageDialog(null, "선택된 코드가 없습니다.");  // 예: 메시지 대화상자로 알림
            extractButton.setIcon(null);
            extractButton.setText("Review Request");
            return;  // 더 이상 진행하지 않고 함수 종료
        }

        SwingUtilities.invokeLater(() -> {
            // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
            String toolWindowId = "GPTResult";
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
            GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();

            Result.initLoadingToolWindowContent(toolWindow);
        });

        // checkedFileSourceCodeList의 각 요소에 대해 요청을 보냅니다.
        for (Map.Entry<String, String> entry : checkedSourceCodeMap.entrySet()) {
            // List<String>을 JSON으로 변환합니다.
            TokenRequest tokenRequest = new TokenRequest(token,user,entry.getValue(),model);
            String jsonPayload = gson.toJson(tokenRequest);
            new Thread(() -> {
                String entryKey = entry.getKey();
                MyRestClient.postAsync(url, jsonPayload, new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        // 정상 응답 시 콜백 함수
                        String responseBody = response.body().string();
                        System.out.println("responseBody:" +responseBody);
                        Gson gson = new Gson();
                        Type type = new TypeToken<HashMap<String, CompletionChatResponse>>(){}.getType();
                        HashMap<String, CompletionChatResponse> responseChat = gson.fromJson(responseBody, type);

                        //답변을 파싱해서 나눠서 저장
                        //String category = messages.toString();
                        categoryData3(entryKey,responseChat);

                        // 여기서 하나씩 로딩 (타임아웃이 없다고 가정)
                        int allResSize = repo.getCodeInfoList().size(); // 모든 응답이 오면 창을 보여주기위해
                        int Id = repo.getCodeCommentsMap().size()-1; //Comment를 필수값이므로 현재 응답 번호 0,1,...

                        //String resCodeName = repo.getCodeInfoList().get(Id).getName(); // 이 응답이 순서대로 들어올 것이므로, Id번째가, 현재 응답의 코드 이름
                        String resCodeName = entryKey;
                        System.out.println();
                        System.out.println("response ID : " + Id + " response Name : " + resCodeName);
                        System.out.println("getCodeReviewMap : " + repo.getCodeReviewMap().size());
                        System.out.println("getCommitMessageMap : " + repo.getCommitMessageMap().size());

                        System.out.println("getCleanCodeMap : " + repo.getCleanCodeMap().size());
                        System.out.println("getCodeCommentsMap : " + repo.getCodeCommentsMap().size());
                        System.out.println("getCodeInfoMap : " + repo.getCodeInfoMap().size());
                        System.out.println("getCodeInfoList : " + repo.getCodeInfoList().size());

                        System.out.println("map data : " + repo.getCleanCodeMap().get(resCodeName));
                        SwingUtilities.invokeLater(() -> {
                            // UI 컴포넌트를 안전하게 업데이트하는 메서드 호출 경주님 코드 호출
                            String toolWindowId = "GPTResult";
                            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
                            GPTResultWindowFactory Result = GPTResultWindowFactory.getInstance();

                            Result.loadingToolWindowContent(resCodeName,toolWindow); //최근 응답을 넣어준다.

                            if(Id == allResSize-1 ){ // 모든 응답이 들어오면 창을 열어 알려준다.
                                System.out.println("Response End");
                                toolWindow.show();
                            }
                            // 모두 다 도착했을때 호출하도록 변경해야 한다.
                            extractButton.setIcon(null);
                            extractButton.setText("Review Request");
                        });
                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();

                        SwingUtilities.invokeLater(() -> {
                            extractButton.setIcon(null);
                            extractButton.setText("Review Request");
                        });
                        categoryData("데이터가 잘못 왔음", entryKey);
                    }
                });
            }).start();
        }
    }//request 보내는거 end

    public void categoryData3(String entryKey, HashMap<String, CompletionChatResponse> responseChat) {

        //ResponseRepository.getInstance().getResponseMap().put(entryKey, category);
        ResponseRepository repo = ResponseRepository.getInstance();
        List<String> messages = responseChat.get("comment").getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());
        System.out.println(messages.toString());
        repo.getCodeCommentsMap().put(entryKey, messages.toString());
        messages.clear();

        messages = responseChat.get("cleanCode").getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());
        System.out.println(messages.toString());
        repo.getCleanCodeMap().put(entryKey, messages.toString());
        messages.clear();

        messages = responseChat.get("codeReview").getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());
        System.out.println(messages.toString());
        repo.getCodeReviewMap().put(entryKey, messages.toString());
        messages.clear();

        messages = responseChat.get("commitMessage").getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());
        System.out.println(messages.toString());
        repo.getCommitMessageMap().put(entryKey, messages.toString());

    }// category2 end

}//class end
