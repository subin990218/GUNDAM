package com.mobilesuit.clientplugin.websocket.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SessionInfo {
    private String userName;
    //private String channel;
    private String status;
    private String onFile;
}
