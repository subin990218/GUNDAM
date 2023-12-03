package com.mobilesuit.mainserver.openaitest.dto.openai.java.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRequest {

    String token;
    String user;
    String message;
    String model;
}