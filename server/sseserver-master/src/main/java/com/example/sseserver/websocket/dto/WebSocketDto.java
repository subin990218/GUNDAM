package com.example.sseserver.websocket.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

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
