package com.mobilesuit.clientplugin.websocket.dto;

import lombok.*;

public class WebSocketDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Event{
        private String code;
        private String text;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class UserInfo{
        private String userName;
        private int code;
        private String target;
    }
}
