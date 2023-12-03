package com.example.sseserver.websocket.session;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionInfo {
    private String userName;
    //private String channel;
    private String status;
    private String onFile;
}
