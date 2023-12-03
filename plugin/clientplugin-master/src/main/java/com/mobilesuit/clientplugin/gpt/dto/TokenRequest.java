package com.mobilesuit.clientplugin.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TokenRequest {

    String token;
    String user;
    String message;
    String model;
}
