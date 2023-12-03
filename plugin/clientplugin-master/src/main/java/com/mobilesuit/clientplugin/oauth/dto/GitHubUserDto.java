package com.mobilesuit.clientplugin.oauth.dto;

import com.intellij.openapi.components.Service;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubUserDto {
    private String login;
    private long id;
    private String node_id;
    private String avatar_url;
    private String url;
    private String repos_url;
    private String events_url;
    private String received_events_url;
    private String type;
    private String name;
}
