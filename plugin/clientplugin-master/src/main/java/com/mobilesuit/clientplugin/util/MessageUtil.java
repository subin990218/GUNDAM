package com.mobilesuit.clientplugin.util;

import com.mobilesuit.clientplugin.client.SocketClient;

public class MessageUtil {
    private static final SocketClient socketClient = SocketClient.getInstance();

    public static void sendMessage(String userName, String message) {
        socketClient.sendMessage(userName,message);
    }
}
