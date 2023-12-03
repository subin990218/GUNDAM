package com.mobilesuit.clientplugin.gpt.client;

import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class PluginWebSocketClient extends WebSocketClient {

    public PluginWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to the server");
    }

    // 응답을 받을때 자동적으로 호출되는 메소드 여기에 할일을 지정하면 될듯?
    @Override
    public void onMessage(String message) {
        System.out.println("Received: " + message);
        ResponseRepository.getInstance().getResponseList().add(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}