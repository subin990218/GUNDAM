package com.mobilesuit.mainserver.openaitest.handler;

import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionChatRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.response.CompletionChatResponse;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CodeReviweHandler extends TextWebSocketHandler {

    private String token = "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";
    private final OpenAiService openAiService = new OpenAiService(token, Duration.ofSeconds(60));
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 클라이언트로부터 받은 메시지 출력
        System.out.println("Received message: " + message.getPayload());
        System.out.println("------------------------------------------");
        CompletionChatResponse completionChatResponse = completionChat(message.getPayload());

        List<String> responeMessages = completionChatResponse.getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());
        String receiveText = responeMessages.toString();
        // 클라이언트에게 메시지 전송
        try {
            session.sendMessage(new TextMessage(receiveText));
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }

    public CompletionChatResponse completionChat(String message) {

        GPTCompletionChatRequest gptCompletionChatRequest =
                new GPTCompletionChatRequest("user123","gpt-3.5-turbo", "user",
                        message+"\n 다음 코드에 대해 주석과 코드 리뷰를 수행해 주세요", 1000);

        //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
        ChatCompletionResult chatCompletion = openAiService.createChatCompletion(
                GPTCompletionChatRequest.of(gptCompletionChatRequest));

        //ChatCompletionResult 답변을 내가 선언한 클래스 형식으로 저장
        CompletionChatResponse response = CompletionChatResponse.of(chatCompletion);

        List<String> messages = response.getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());

        System.out.println(messages.toString());
        // DB 저장 위해 entitiy에 답변 저장

        return response;
    }

    public void getText(){
        System.out.println("tttest");
    }
}




