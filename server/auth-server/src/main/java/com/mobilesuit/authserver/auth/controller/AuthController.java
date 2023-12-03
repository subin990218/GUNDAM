package com.mobilesuit.authserver.auth.controller;

import com.google.gson.Gson;
import com.mobilesuit.authserver.auth.dto.GitHubOauthDto;
import com.mobilesuit.authserver.auth.service.AuthService;
import com.mobilesuit.authserver.auth.token.TokenTemp;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenTemp tokenTemp;

    // 기존의 서버에서 브라우저로 요청을 받아 로컬 웹서버에 다시 json 으로 토큰을 전송하는 방식 (안전한가? 잘모르겠음 https 써야함)

    /*@GetMapping("/auth/github/callback")
    public ModelAndView getCode(@RequestParam String code, RedirectAttributes redirectAttributes, HttpSession session) throws IOException {

        String token = authService.getTokenFromCode(code);

        tokenTemp.addToken(session.getId(),token);

        ModelAndView mav = new ModelAndView("loginsuccess");

        return mav;
    }*/

    @GetMapping("/auth/github/callback")
    public ModelAndView getCode(@RequestParam String code, RedirectAttributes redirectAttributes, HttpSession session,
                                HttpServletRequest request,HttpServletResponse response) throws IOException {

        String token = authService.getTokenFromCode(code);

        tokenTemp.addToken(session.getId(),token);

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        Cookie[] cookies = request.getCookies();
        Cookie delCook;
        String cookieValue = "phone";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("device".equals(cookie.getName())) {
                    cookieValue = cookie.getValue();
                    System.out.println(cookieValue);
                    delCook = cookie;
                    break;
                }
            }
        }

        Gson gson = new Gson();
        GitHubOauthDto.Receive gitHubOauthDto = gson.fromJson(token, GitHubOauthDto.Receive.class);
        if(cookieValue.equals("plugin")){
            redirectStrategy.sendRedirect(request,response,"http://localhost:23137/oauth?token="+gitHubOauthDto.getAccess_token());
        }else{
            redirectStrategy.sendRedirect(request,response,"https://k9e207.p.ssafy.io/oauth?token="+gitHubOauthDto.getAccess_token());
        }

        ModelAndView mav = new ModelAndView("loginsuccess");

        return mav;
    }
}
