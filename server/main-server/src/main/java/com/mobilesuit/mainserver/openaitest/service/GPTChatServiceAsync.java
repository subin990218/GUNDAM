package com.mobilesuit.mainserver.openaitest.service;

import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionChatRequest2;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.TokenRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.response.CompletionChatResponse;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GPTChatServiceAsync {

    @Async
    public CompletableFuture<CompletionChatResponse> promptLineCommentAsync(TokenRequest tokenRequest) {

        return CompletableFuture.supplyAsync(() -> {
            OpenAiService openAiServiceUserToken = new OpenAiService(tokenRequest.getToken(), Duration.ofSeconds(180));
            // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
            // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
            String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다. \n" +
                    "코드의 각 부분을 라인별로 코드 위에 주석을 달아주셔야 합니다" +
                    "\n";

            String responseRule = "응답 원칙:\n" +
                    "1. 핵심 원칙: 응답은 핵심적인 정보만을 전달.\n" +
                    "2. 언어 원칙: 모든 응답은 한국어로 이루어져야 합니다.\n" +
                    "3. 주석 제시 방법: 작성하는 코드의 주석은 코드 사이에 중요 부분을 설명하며 코드의 주석을 작성합니다. \n" +
                    "4. 코드 제시 방법: 작성하는 코드의 목적과 동작 방식을 명확히 설명한 후, 구체적인 코드 스니펫을 제공합니다. 사용자가 추가로 작성해야 할 부분이 없어야 합니다.\n" +
                    "5. 디버깅 원칙: 오류가 있는 코드 라인은 주석처리하고, 그 바로 아래에 수정된 코드를 삽입합니다.\n" +
                    "6. 누적 정보 표시: 사용자의 질문과 제공된 다양한 정보를 응답의 최상단에 작성합니다.\n" +
                    "7. 코드 완성도: 사용자가 바로 사용할 수 있는 완성된 코드를 제공합니다. 부가 설명이나 주석을 최소화하고, 코드 자체로 작동해야 할 기능을 명확히 표현합니다.\n" +
                    "\n" +
                    "<답변 작성 예시>\n" +
                    "코드 주석 : 코드의 각 부분에 대해 주석을 작성 \n";

            LocalDateTime now = LocalDateTime.now();

            GPTCompletionChatRequest2 gptRequestComment =
                    new GPTCompletionChatRequest2(tokenRequest.getUser(), tokenRequest.getModel(), "user",
                            role + responseRule + tokenRequest.getMessage());

            CompletionChatResponse responseCodeReview = null;
            try {
                //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
                ChatCompletionResult chatCompletionCode = openAiServiceUserToken.createChatCompletion(
                        GPTCompletionChatRequest2.of(gptRequestComment));

                responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

                List<String> messages = responseCodeReview.getMessages().stream()
                        .map(CompletionChatResponse.Message::getMessage)
                        .collect(Collectors.toList());

                System.out.println("코드 주석 대한 답변 \n");
                System.out.println(messages.toString());

            } catch (OpenAiHttpException e) {
                // OpenAI에서 반환한 HTTP 에러 처리
                log.error("OpenAI API 에러: {}", e.getMessage());
                return null;
                // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
            } catch (Exception e) {
                // 다른 예상하지 못한 예외 처리
                log.error("예상하지 못한 에러 발생: {}", e.getMessage());
                return null;
            }
            return responseCodeReview;
        });
    }


    @Async
    public CompletableFuture<CompletionChatResponse> promptCleanCodeAsync(TokenRequest tokenRequest) {

        return CompletableFuture.supplyAsync(() -> {
            OpenAiService openAiServiceUserToken = new OpenAiService(tokenRequest.getToken(), Duration.ofSeconds(180));
            // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
            // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
            String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다.  코드를 클린 코드를 기반으로 적용한 결과 코드를 보여주셔야 합니다. "+
                    "\n";

            String responseRule = "응답 원칙:\n" +
                    "1. 핵심 원칙: 응답은 핵심적인 정보만을 전달.\n" +
                    "2. 언어 원칙: 모든 응답은 한국어로 이루어져야 합니다.\n" +
                    "3. 주석 제시 방법: 작성하는 코드의 주석은 코드 사이에 중요 부분을 설명하며 코드의 주석을 작성합니다. \n" +
                    "4. 코드 제시 방법: 작성하는 코드의 목적과 동작 방식을 명확히 설명한 후, 구체적인 코드 스니펫을 제공합니다. 사용자가 추가로 작성해야 할 부분이 없어야 합니다.\n" +
                    "5. 디버깅 원칙: 오류가 있는 코드 라인은 주석처리하고, 그 바로 아래에 수정된 코드를 삽입합니다.\n" +
                    "6. 누적 정보 표시: 사용자의 질문과 제공된 다양한 정보를 응답의 최상단에 작성합니다.\n" +
                    "7. 코드 완성도: 사용자가 바로 사용할 수 있는 완성된 코드를 제공합니다. 부가 설명이나 주석을 최소화하고, 코드 자체로 작동해야 할 기능을 명확히 표현합니다.\n" +
                    "\n" +
                    "<답변 작성 예시>\n" +
                    "클린 코드 : \n";

            LocalDateTime now = LocalDateTime.now();

            GPTCompletionChatRequest2 gptRequestComment =
                    new GPTCompletionChatRequest2(tokenRequest.getUser(), tokenRequest.getModel(), "user",
                            role + responseRule + tokenRequest.getMessage());

            CompletionChatResponse responseCodeReview = null;
            try {
                //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
                ChatCompletionResult chatCompletionCode = openAiServiceUserToken.createChatCompletion(
                        GPTCompletionChatRequest2.of(gptRequestComment));

                responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

                List<String> messages = responseCodeReview.getMessages().stream()
                        .map(CompletionChatResponse.Message::getMessage)
                        .collect(Collectors.toList());

                System.out.println("클린코드에 대한 답변 \n");
                System.out.println(messages.toString());

            } catch (OpenAiHttpException e) {
                // OpenAI에서 반환한 HTTP 에러 처리
                log.error("OpenAI API 에러: {}", e.getMessage());
                return null;
                // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
            } catch (Exception e) {
                // 다른 예상하지 못한 예외 처리
                log.error("예상하지 못한 에러 발생: {}", e.getMessage());
                return null;
            }
            return responseCodeReview;
        });
    }


    @Async
    public CompletableFuture<CompletionChatResponse> promptCodeReviewAsync(TokenRequest tokenRequest) {

        return CompletableFuture.supplyAsync(() -> {
            OpenAiService openAiServiceUserToken = new OpenAiService(tokenRequest.getToken(), Duration.ofSeconds(180));
            // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
            // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
            String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다. 당신은 전달 받은 코드에 대한 코드 리뷰를 작성해 주셔야 합니다." +
                    "\n";

            String responseRule = "응답 원칙:\n" +
                    "1. 핵심 원칙: 응답은 핵심적인 정보만을 전달.\n" +
                    "2. 언어 원칙: 모든 응답은 한국어로 이루어져야 합니다.\n" +
                    "3. 누적 정보 표시: 사용자의 질문과 제공된 다양한 정보를 응답의 최상단에 작성합니다.\n" +
                    "4. 코드 완성도: 사용자가 바로 사용할 수 있는 완성된 코드를 제공합니다. 부가 설명이나 주석을 최소화하고, 코드 자체로 작동해야 할 기능을 명확히 표현합니다.\n" +
                    "\n" +
                    "<답변 작성 예시>\n" +
                    "코드 리뷰 : 코드에 보완할 점이 있다면 작성";

            LocalDateTime now = LocalDateTime.now();

            GPTCompletionChatRequest2 gptRequestComment =
                    new GPTCompletionChatRequest2(tokenRequest.getUser(), tokenRequest.getModel(), "user",
                            role + responseRule + tokenRequest.getMessage());

            CompletionChatResponse responseCodeReview = null;
            try {
                //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
                ChatCompletionResult chatCompletionCode = openAiServiceUserToken.createChatCompletion(
                        GPTCompletionChatRequest2.of(gptRequestComment));

                responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

                List<String> messages = responseCodeReview.getMessages().stream()
                        .map(CompletionChatResponse.Message::getMessage)
                        .collect(Collectors.toList());

                System.out.println("코드 리뷰에 대한 답변 \n");
                System.out.println(messages.toString());

            } catch (OpenAiHttpException e) {
                // OpenAI에서 반환한 HTTP 에러 처리
                log.error("OpenAI API 에러: {}", e.getMessage());
                return null;
                // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
            } catch (Exception e) {
                // 다른 예상하지 못한 예외 처리
                log.error("예상하지 못한 에러 발생: {}", e.getMessage());
                return null;
            }
            return responseCodeReview;
        });
    }

    @Async
    public CompletableFuture<CompletionChatResponse> promptCommitMessageAsync(TokenRequest tokenRequest) {
        return CompletableFuture.supplyAsync(() -> {
            OpenAiService openAiServiceUserToken = new OpenAiService(tokenRequest.getToken(), Duration.ofSeconds(120));
            // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
            // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
            String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다. 당신은 전달 받은 코드에 대한 commit을 수행할때 작성할 commit 메시지를 추천해 주셔야 합니다. \n" +
                    "\n";

            String responseRule = "응답 원칙:\n" +
                    "1. 핵심 원칙: 응답은 핵심적인 정보만을 전달.\n" +
                    "2. 누적 정보 표시: 사용자의 질문과 제공된 다양한 정보를 응답의 최상단에 작성합니다.\n" +
                    "<답변 작성 예시>\n" +
                    "추천 커밋 메시지: 상세한 커밋 메시지 작성 내용\n";

            LocalDateTime now = LocalDateTime.now();

            GPTCompletionChatRequest2 gptRequestComment =
                    new GPTCompletionChatRequest2(tokenRequest.getUser(), tokenRequest.getModel(), "user",
                            role + responseRule + tokenRequest.getMessage());

            CompletionChatResponse responseCodeReview = null;
            try {
                //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
                ChatCompletionResult chatCompletionCode = openAiServiceUserToken.createChatCompletion(
                        GPTCompletionChatRequest2.of(gptRequestComment));

                responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

                List<String> messages = responseCodeReview.getMessages().stream()
                        .map(CompletionChatResponse.Message::getMessage)
                        .collect(Collectors.toList());

                System.out.println("커밋 메시지에 대한 답변 \n");
                System.out.println(messages.toString());

            } catch (OpenAiHttpException e) {
                // OpenAI에서 반환한 HTTP 에러 처리
                log.error("OpenAI API 에러: {}", e.getMessage());
                return null;
                // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
            } catch (Exception e) {
                // 다른 예상하지 못한 예외 처리
                log.error("예상하지 못한 에러 발생: {}", e.getMessage());
                return null;
            }
            return responseCodeReview;
        });
    }


}
