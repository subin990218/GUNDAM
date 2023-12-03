package com.mobilesuit.authserver.auth.token;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenTemp {
    private static final Map<String, String> tokens = new ConcurrentHashMap<>();

    public void addToken(String sessionId, String token){
        tokens.put(sessionId,token);
    }

    public String getToken(String sessionId){
        String token = tokens.get(sessionId);
        tokens.remove(sessionId);
        return token;
    }
}
