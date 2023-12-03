package com.mobilesuit.clientplugin.documentation;

import com.google.gson.Gson;
import com.mobilesuit.clientplugin.documentation.dto.Doc;
import com.mobilesuit.clientplugin.renderer.GptResultPanel;
import com.mobilesuit.clientplugin.util.MyRestClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendMainServer {
    //테스트용
    private static SendMainServer instance = new SendMainServer();
    private SendMainServer(){}
    public static SendMainServer getInstance(){
        return instance;
    }

    public void saveDocElement(Doc doc){ // 정상 1, 비정상 0
        //doc 값이 잘들어왔는지 확인

        System.out.println("-----------saveDocElement--------------");
        //System.out.println(doc.toString());
//        System.out.println("-----------수빈님 아이디가 임시로 들어있다. 이후 지우기--------------");
//        doc.setUserId("subin990218");
//        doc.setRepoName("baekjoon_swea");

        new Thread(() -> {
            System.out.println("Doc를 main 서버로 보냅니다. ...");

            String url = "https://k9e207.p.ssafy.io/api/document/save";
            //String url = "http://localhost:8081/api/document/save";

            // 현재 문서화된 정보를 보낸다.

            Gson gson = new Gson();
            String jsonPayload1 = gson.toJson(doc);
            String jsonPayload2 = gson.toJson(doc.getMPList());


            System.out.println(jsonPayload1);
            System.out.println(jsonPayload2);

            MyRestClient.postAsync2(url, jsonPayload1,jsonPayload2, new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {



                    int a = response.code();
                    System.out.println("문서화 main server 반환 값 : " + response + " : "+a);
                    if(a == 200){
                        SwingUtilities.invokeLater(() -> {
                            GptResultPanel.returnServerReponse(1);
                        });
                        System.out.println("성공");
                    }else if(a == 500){
                        SwingUtilities.invokeLater(() -> {
                            GptResultPanel.returnServerReponse(2); //tjqjsoqndpfj
                        });

                    }else{
                        SwingUtilities.invokeLater(() -> {
                            GptResultPanel.returnServerReponse(0);
                        });
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                 System.out.println("실패");
                }
            });
        }).start();


    }


//    public void sendMDDoc(){
//        new Thread(() -> {
//            System.out.println("파일 내용을 main 서버로 보냅니다. ...");
//
//            String url = "http://localhost:8081/api/document/method";
//
//
//            //현재 생성된 파일을 읽어서 보낸다.
//            String filePath = "D:/E207/gitlab/plugin demo/S09P31E207/CodeDocument/printCodeMap1.md";
//
//            String fileContent = "sdf";
//            try {
//                Path path = Paths.get(filePath);
//                fileContent = Files.readString(path);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Gson gson = new Gson();
//            String jsonPayload = gson.toJson(fileContent);
//            MyRestClient.postAsync(url, jsonPayload, new Callback(){
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    System.out.println("성공");
//                }
//
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    System.out.println("실패");
//                }
//            });
//        }).start();
//
//
//
//    }





}
