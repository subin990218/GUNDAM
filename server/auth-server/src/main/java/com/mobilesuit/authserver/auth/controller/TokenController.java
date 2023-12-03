package com.mobilesuit.authserver.auth.controller;

import com.mobilesuit.authserver.auth.token.TokenTemp;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenTemp tokenTemp;

    @GetMapping("/auth/token")
    public ResponseEntity<String> getToken(HttpSession session){

        return new ResponseEntity<>(tokenTemp.getToken(session.getId()), HttpStatus.OK);
    }
}
