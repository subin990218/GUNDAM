package com.mobilesuit.authserver.auth.handler;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobilesuit.authserver.auth.dto.LoginResponseDto;
import com.mobilesuit.authserver.member.entity.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("# Authenticated successfully!");

        sendSuccessResponse(response,authentication);
    }

    private void sendSuccessResponse(HttpServletResponse response,
                                     Authentication authentication) throws IOException {

       Gson gson = new Gson();
        Member member = (Member) authentication.getPrincipal();
        //System.out.println(member);
        String profileImgUrl = null;
        /*MemberImage memberImage = member.getMemberImage();
        if (memberImage != null) {
            profileImgUrl = memberImage.getFileUrl();
        }*/

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .profileImgUrl(profileImgUrl)
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(loginResponseDto, LoginResponseDto.class));
    }
}
