package com.mobilesuit.mainserver.openaitest.dto.openai.java.request;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GPTCompletionChatRequest2 {

    private String user;

    private String model;

    private String role;

    private String message;

    public static ChatCompletionRequest of(GPTCompletionChatRequest2 request) {
        return ChatCompletionRequest.builder()
                .model(request.getModel())
                .messages(convertChatMessage(request))
                .user(request.getUser())
                .build();
    }

    private static List<ChatMessage> convertChatMessage(GPTCompletionChatRequest2 request) {
        return List.of(new ChatMessage(request.getRole(), request.getMessage()));
    }
}
