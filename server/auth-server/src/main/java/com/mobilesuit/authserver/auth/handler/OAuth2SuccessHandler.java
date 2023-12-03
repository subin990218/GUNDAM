package com.mobilesuit.authserver.auth.handler;

import com.google.gson.Gson;
import com.mobilesuit.authserver.auth.dto.GitHubOauthDto;
import com.mobilesuit.authserver.auth.jwt.JwtTokenizer;
import com.mobilesuit.authserver.auth.redis.RedisDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String oauthClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String oauthClientSecret;



    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요성공이요?");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        System.out.println("ID");
        System.out.println(request.getSession().getId());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication){
        try {
            System.out.println("onAuthenticationSuccess");

            System.out.println(request.getRequestURI());

            String code = request.getQueryString().split("&")[0].replace("code=", "");


            System.out.println(request.getQueryString());
        /*String state = request.getQueryString().split("&")[1].replace("state=","");
        System.out.println(code);

        Gson gson = new Gson();

        GitHubOauthDto.Receive gitHubOauthDto = gson.fromJson(getAccessToken(code,state), GitHubOauthDto.Receive.class);

        int httpStatusCode = sendTokenToPluginServer(gitHubOauthDto);*/
         }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int sendTokenToPluginServer(GitHubOauthDto.Receive gitHubOauthDto) {
        String url = "http://localhost:23137/oauth";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GitHubOauthDto.Receive> entity = new HttpEntity<>(gitHubOauthDto, headers);

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getStatusCode().value();
    }

    private String getAccessToken(String code,String state) {
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
