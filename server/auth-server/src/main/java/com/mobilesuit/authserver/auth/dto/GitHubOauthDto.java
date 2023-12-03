package com.mobilesuit.authserver.auth.dto;

import lombok.*;


public class GitHubOauthDto {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Receive{
        private String access_token;
        private long expires_in;
        private String refresh_token;
        private long refresh_token_expires_in;
        private String scope;
        private String token_type;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Send{
        private String client_id;
        private String client_secret;
        private String code;
    }
}
