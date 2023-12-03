package com.mobilesuit.clientplugin.util;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class MyRestClient {
    public static void getAsync(String requestURL, Callback myCallback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES).build();

        try {
            Request request = new Request.Builder()
                    .url(requestURL)
//                    .addHeader(key, value)
                    .build();

            client.newCall(request).enqueue(myCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAsync(String requestURL,Headers headers, Callback myCallback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(6, TimeUnit.MINUTES)
                .readTimeout(6, TimeUnit.MINUTES)
                .writeTimeout(6, TimeUnit.MINUTES).build();

        try {
            Request request = new Request.Builder()
                    .url(requestURL)
                    .headers(headers)
                    .build();

            client.newCall(request).enqueue(myCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postAsync(String requestURL, String jsonMessage, Callback myCallback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(6, TimeUnit.MINUTES)
                .readTimeout(6, TimeUnit.MINUTES)
                .writeTimeout(6, TimeUnit.MINUTES).build();
        try {
            RequestBody requestBody = RequestBody.create(
                    jsonMessage,
                    MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(requestURL)
    //                .addHeader(key, value)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(myCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postAsync2(String requestURL, String jsonMessage1,String jsonMessage2, Callback myCallback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES).build();
        try {

            RequestBody requestBody1 = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonMessage1
            );

            // Create RequestBody for jsonMessage2
            RequestBody requestBody2 = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonMessage2
            );

            // Combine the two RequestBodies into a MultipartBody
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("part1", null, requestBody1)
                    .addFormDataPart("part2", null, requestBody2)
                    .build();

            Request request = new Request.Builder()
                    .url(requestURL)
                    .addHeader("Content-Type", "multipart/form-data") //
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(myCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // postAsync method overloading with headers
    public static void postAsync(String requestURL, String jsonMessage, Headers headers, Callback myCallback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES).build();
        try {
            RequestBody requestBody = RequestBody.create(
                    jsonMessage,
                    MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(requestURL)
                    .post(requestBody)
                    .headers(headers)
                    .build();

            client.newCall(request).enqueue(myCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
