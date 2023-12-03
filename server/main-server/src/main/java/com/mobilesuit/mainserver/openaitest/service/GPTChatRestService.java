package com.mobilesuit.mainserver.openaitest.service;


import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionChatRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionChatRequest2;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.TokenRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.response.CompletionChatResponse;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.response.CompletionResponse;
import com.mobilesuit.mainserver.openaitest.entity.answer.GPTAnswer;
import com.mobilesuit.mainserver.openaitest.entity.answer.GPTAnswerRepository;
import com.mobilesuit.mainserver.openaitest.entity.question.GPTQuestion;
import com.mobilesuit.mainserver.openaitest.entity.question.GPTQuestionRepository;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GPTChatRestService {
    private final OpenAiService openAiService;

    private final GPTQuestionRepository questionRepository;

    private final GPTAnswerRepository answerRepository;
    private LocalDateTime lastExecutionTime = null;

    private final String[] modelOptions = {
          "gpt-3.5-turbo",
          //"gpt-4-32k", // 주석 처리된 모델
          "gpt-3.5-turbo-16k",
          "gpt-4"
    };
    @Transactional
  public CompletionResponse completion(final GPTCompletionRequest restRequest) {
    CompletionResult result = openAiService.createCompletion(GPTCompletionRequest.of(restRequest));
    CompletionResponse response = CompletionResponse.of(result);

    List<String> messages = response.getMessages().stream()
        .map(CompletionResponse.Message::getText)
        .collect(Collectors.toList());

    GPTAnswer gptAnswer = saveAnswer(messages);
    saveQuestion(restRequest.getPrompt(), gptAnswer);

    return response;
  }

  //openAI 에 chat api 요청
  @Transactional
  public CompletionChatResponse completionChat(GPTCompletionChatRequest gptCompletionChatRequest) {
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
    GPTAnswer gptAnswer = saveAnswer(messages);

    saveQuestion(gptCompletionChatRequest.getMessage(), gptAnswer);

    return response;
  }

  @Transactional
  public HashMap<String,CompletionChatResponse> singleCompletionCommentAndCodeReview(String message) {
    // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
    // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄

    GPTCompletionChatRequest gptRequestComment =
            new GPTCompletionChatRequest("user","gpt-3.5-turbo", "user",
                    message+ "다음 코드에 대한 설명을 작성해 주세요", 1000);

    GPTCompletionChatRequest gptRequestCodeReview =
            new GPTCompletionChatRequest("user","gpt-3.5-turbo", "user",
                    message+ "다음 코드에 대한 코드 리뷰를 작성해 주세요", 1000);

    HashMap<String, CompletionChatResponse> responseHashMap = new HashMap<>();
    try {
    //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
      ChatCompletionResult chatCompletion = openAiService.createChatCompletion(
            GPTCompletionChatRequest.of(gptRequestComment));
      ChatCompletionResult chatCompletion2 = openAiService.createChatCompletion(
            GPTCompletionChatRequest.of(gptRequestCodeReview));

    //ChatCompletionResult 답변을 내가 선언한 클래스 형식으로 저장
      CompletionChatResponse responseComment = CompletionChatResponse.of(chatCompletion);

      CompletionChatResponse responseCodeReview = CompletionChatResponse.of(chatCompletion2);

      List<String> messages = responseComment.getMessages().stream()
            .map(CompletionChatResponse.Message::getMessage)
            .collect(Collectors.toList());

      List<String> messages2 = responseCodeReview.getMessages().stream()
            .map(CompletionChatResponse.Message::getMessage)
            .collect(Collectors.toList());

      System.out.println(messages.toString());
      System.out.println("-----------------");
      System.out.println(messages2.toString());

      responseHashMap.put("comment", responseComment);
      responseHashMap.put("codeReview", responseCodeReview);

    } catch (OpenAiHttpException e) {
    // OpenAI에서 반환한 HTTP 에러 처리
      log.error("OpenAI API 에러: {}", e.getMessage());
      responseHashMap.clear();
    // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
    } catch (Exception e) {
    // 다른 예상하지 못한 예외 처리
      log.error("예상하지 못한 에러 발생: {}", e.getMessage());
      responseHashMap.clear();
    }
    return responseHashMap;
  }

  @Transactional
  public CompletionChatResponse promptTest(String message) {

    // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
    // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
    String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다. 당신은 전달 받는 코드를 클린 코드를 기반으로 적용한 결과 코드를 보여주셔야 하며, " +
            "코드가 어떤 의미를 갖는지  설명해주셔야 합니다. 마지막으로 해당 코드에 대한 commit을 수행할때 작성할 commit 메시지를 추천해 주셔야 합니다. \n" +
            "\n";

    String responseRule = "응답 원칙:\n" +
            "1. 핵심 원칙: 응답은 핵심적인 정보만을 전달.\n" +
            "2. 언어 원칙: 모든 응답은 한국어로 이루어져야 합니다.\n" +
            "3. 코드 제시 방법: 작성하는 코드의 목적과 동작 방식을 명확히 설명한 후, 구체적인 코드 스니펫을 제공합니다. 사용자가 추가로 작성해야 할 부분이 없어야 합니다.\n" +
            "4. 디버깅 원칙: 오류가 있는 코드 라인은 주석처리하고, 그 바로 아래에 수정된 코드를 삽입합니다.\n" +
            "5. 누적 정보 표시: 사용자의 질문과 제공된 다양한 정보를 응답의 최상단에 작성합니다.\n" +
            "6. 코드 완성도: 사용자가 바로 사용할 수 있는 완성된 코드를 제공합니다. 부가 설명이나 주석을 최소화하고, 코드 자체로 작동해야 할 기능을 명확히 표현합니다.\n" +
            "\n" +
            "<답변 작성 예시>\n" +
            "코드 설명 : 코드의 목적과 결과에 대해 상세히 작성 \n" +
            "코드 리뷰 : 코드에 보완할 점이 있다면 작성\n" +
            "클린 코드 규칙이 적용된 코드 : \n" +
            "추천 커밋 메시지: 상세한 커밋 메시지 작성 내용\n";

    LocalDateTime now = LocalDateTime.now();

    GPTCompletionChatRequest2 gptRequestComment =
            new GPTCompletionChatRequest2("user222","gpt-3.5-turbo", "user",
                    role+responseRule+message );

    CompletionChatResponse responseCodeReview = null;
    try {
      //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
      ChatCompletionResult chatCompletionCode = openAiService.createChatCompletion(
              GPTCompletionChatRequest2.of(gptRequestComment));

      responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

      List<String> messages = responseCodeReview.getMessages().stream()
              .map(CompletionChatResponse.Message::getMessage)
              .collect(Collectors.toList());

      System.out.println(messages.toString());

    } catch (OpenAiHttpException e) {
      // OpenAI에서 반환한 HTTP 에러 처리
      log.error("OpenAI API 에러: {}", e.getMessage());

      // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
    } catch (Exception e) {
      // 다른 예상하지 못한 예외 처리
      log.error("예상하지 못한 에러 발생: {}", e.getMessage());
    }
    return responseCodeReview;
  }

  @Transactional
  public CompletionChatResponse promptUserToken(TokenRequest tokenRequest) {

    OpenAiService openAiServiceUserToken = new OpenAiService(tokenRequest.getToken(), Duration.ofSeconds(300));
    // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
    // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
    String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다. 당신은 전달 받는 코드를 클린 코드를 기반으로 적용한 결과 코드를 보여주셔야 하며, " +
            "코드가 어떤 의미를 갖는지  설명해주셔야 합니다. 마지막으로 해당 코드에 대한 commit을 수행할때 작성할 commit 메시지를 추천해 주셔야 합니다. \n" +
            "\n";

    String responseRule = "응답 원칙:\n" +
            "1. 핵심 원칙: 응답은 핵심적인 정보만을 전달.\n" +
            "2. 언어 원칙: 모든 응답은 한국어로 이루어져야 합니다.\n" +
            "3. 코드 제시 방법: 작성하는 코드의 목적과 동작 방식을 명확히 설명한 후, 구체적인 코드 스니펫을 제공합니다. 사용자가 추가로 작성해야 할 부분이 없어야 합니다.\n" +
            "4. 디버깅 원칙: 오류가 있는 코드 라인은 주석처리하고, 그 바로 아래에 수정된 코드를 삽입합니다.\n" +
            "5. 누적 정보 표시: 사용자의 질문과 제공된 다양한 정보를 응답의 최상단에 작성합니다.\n" +
            "6. 코드 완성도: 사용자가 바로 사용할 수 있는 완성된 코드를 제공합니다. 부가 설명이나 주석을 최소화하고, 코드 자체로 작동해야 할 기능을 명확히 표현합니다.\n" +
            "\n" +
            "<답변 작성 예시>\n" +
            "코드 설명 : 코드의 목적과 결과에 대해 상세히 작성 \n" +
            "코드 리뷰 : 코드에 보완할 점이 있다면 작성\n" +
            "클린 코드 규칙이 적용된 코드 : \n" +
            "추천 커밋 메시지: 상세한 커밋 메시지 작성 내용\n";

    LocalDateTime now = LocalDateTime.now();

    GPTCompletionChatRequest2 gptRequestComment =
            new GPTCompletionChatRequest2(tokenRequest.getUser(),tokenRequest.getModel(), "user",
                    role+responseRule+tokenRequest.getMessage() );

    CompletionChatResponse responseCodeReview = null;
    try {
      //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
      ChatCompletionResult chatCompletionCode = openAiServiceUserToken.createChatCompletion(
              GPTCompletionChatRequest2.of(gptRequestComment));

      responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

      List<String> messages = responseCodeReview.getMessages().stream()
              .map(CompletionChatResponse.Message::getMessage)
              .collect(Collectors.toList());

      System.out.println(messages.toString());

    } catch (OpenAiHttpException e) {
      // OpenAI에서 반환한 HTTP 에러 처리
      log.error("OpenAI API 에러: {}", e.getMessage());

      // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
    } catch (Exception e) {
      // 다른 예상하지 못한 예외 처리
      log.error("예상하지 못한 에러 발생: {}", e.getMessage());
    }
    return responseCodeReview;
  }

  @Transactional
  public CompletionChatResponse promptLineComment(TokenRequest tokenRequest) {

    OpenAiService openAiServiceUserToken = new OpenAiService(tokenRequest.getToken(), Duration.ofSeconds(300));
    // 클래스냐 메소드에 따라 길이 제한 있음 메소드도 길이가 글면 주석 길이를 길게 받음
    // 현재는 하나의 클래스 혹은 메소드만 받도록 코드 구현 메소드나 클래스가 여러개면 여러개의 요청을 보냄
    String role = "당신은 전문 프로그래머이며 개발자 코드 검토자 역할을 할 것입니다. 당신은 전달 받는 코드를 클린 코드를 기반으로 적용한 결과 코드를 보여주셔야 하며, " +
            "코드 라인별로 주석을 달아주셔야 하고, 마지막으로 해당 코드에 대한 commit을 수행할때 작성할 commit 메시지를 추천해 주셔야 합니다. \n" +
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
            "코드 주석 : 코드의 각 부분에 대해 주석을 작성 \n" +
            "코드 리뷰 : 코드에 보완할 점이 있다면 작성\n" +
            "클린 코드 규칙이 적용된 코드 : \n" +
            "추천 커밋 메시지: 상세한 커밋 메시지 작성 내용\n";

    LocalDateTime now = LocalDateTime.now();

    GPTCompletionChatRequest2 gptRequestComment =
            new GPTCompletionChatRequest2(tokenRequest.getUser(),tokenRequest.getModel(), "user",
                    role+responseRule+tokenRequest.getMessage() );

    CompletionChatResponse responseCodeReview = null;
    try {
      //openai-java에서 발송한 답변을  ChatCompletionResult 에 저장
      ChatCompletionResult chatCompletionCode = openAiServiceUserToken.createChatCompletion(
              GPTCompletionChatRequest2.of(gptRequestComment));

      responseCodeReview = CompletionChatResponse.of(chatCompletionCode);

      List<String> messages = responseCodeReview.getMessages().stream()
              .map(CompletionChatResponse.Message::getMessage)
              .collect(Collectors.toList());

      System.out.println(messages.toString());

    } catch (OpenAiHttpException e) {
      // OpenAI에서 반환한 HTTP 에러 처리
      log.error("OpenAI API 에러: {}", e.getMessage());

      // 다른 로직이 필요한 경우 추가하세요. (예: 에러 메시지를 클라이언트에 전송)
    } catch (Exception e) {
      // 다른 예상하지 못한 예외 처리
      log.error("예상하지 못한 에러 발생: {}", e.getMessage());
    }
    return responseCodeReview;
  }

  @Transactional
  public List<String> completionChatBroadCast(String message) {
    List<GPTCompletionChatRequest> chatRequests = new ArrayList<>();

//    for (String modelOption : modelOptions) {
//      chatRequests.add(new GPTCompletionChatRequest(modelOption, "user", message, 1000));
//    }
//
    List<String> list = new ArrayList<>();
//    for (int i=0;i<chatRequests.size();i++) {
//      ChatCompletionResult chatCompletion = openAiService.createChatCompletion
//              (GPTCompletionChatRequest.of(chatRequests.get(i)));
//
//      CompletionChatResponse response = CompletionChatResponse.of(chatCompletion);
//      List<String> messages = response.getMessages().stream()
//              .map(CompletionChatResponse.Message::getMessage)
//              .collect(Collectors.toList());
//
//      System.out.println(messages);
//      list.add(String.join(", ", messages));
//    }
    return list;
  }

  private void saveQuestion(String question, GPTAnswer answer) {
    GPTQuestion questionEntity = new GPTQuestion(question, answer);
    questionRepository.save(questionEntity);
  }

  private GPTAnswer saveAnswer(List<String> response) {

    String answer = response.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.joining());

    return answerRepository.save(new GPTAnswer(answer));
  }

}
