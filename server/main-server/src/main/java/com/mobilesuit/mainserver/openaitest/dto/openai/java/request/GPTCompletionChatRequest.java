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
public class GPTCompletionChatRequest {

  private String user;

  private String model;

  private String role;

  private String message;

  private Integer maxTokens;


  public static ChatCompletionRequest of(GPTCompletionChatRequest request) {
    return ChatCompletionRequest.builder()
        .model(request.getModel())
        .messages(convertChatMessage(request))
        .maxTokens(request.getMaxTokens())
            .user(request.getUser())
        .build();
  }

  private static List<ChatMessage> convertChatMessage(GPTCompletionChatRequest request) {
    return List.of(new ChatMessage(request.getRole(), request.getMessage()));
  }
}
