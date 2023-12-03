package com.mobilesuit.authserver.auth.service;

import com.google.gson.Gson;
import com.mobilesuit.authserver.auth.dto.GitHubOauthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String oauthClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String oauthClientSecret;

    public String getTokenFromCode(String code){

        Gson gson = new Gson();

        String str = getAccessToken(code);

        GitHubOauthDto.Receive gitHubOauthDto = gson.fromJson(str, GitHubOauthDto.Receive.class);

        //int httpStatusCode = sendTokenToPluginServer(gitHubOauthDto);

        return str;
    }

    private int sendTokenToPluginServer(GitHubOauthDto.Receive gitHubOauthDto) {
        String url = "http://localhost:23137/oauth";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GitHubOauthDto.Receive> entity = new HttpEntity<>(gitHubOauthDto, headers);

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getStatusCode().value();
    }

    private String getAccessToken(String code) {
        String url = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id",oauthClientId);
        params.add("client_secret",oauthClientSecret);
        params.add("code",code);


        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        System.out.println(entity.toString());
        System.out.println(response.getBody());

        return response.getBody();
    }

}
