package com.mobilesuit.clientplugin.client;

import com.intellij.openapi.components.Service;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Service
public class SSEClient {

    private static final SSEClient instance = new SSEClient();

    private SSEClient(){
        OkHttpClient client = new OkHttpClient(); //k9e207.p.ssafy.io
        Request request = new Request.Builder().url("http://k9e207a.p.ssafy.io:8090/sse").build();
        //Request request = new Request.Builder().url("http://localhost:8080/sse").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    for (String line; (line = body.source().readUtf8Line()) != null;) {
                        System.out.println(line);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static SSEClient getInstance(){
        return instance;
    }



}
