package com.mobilesuit.mainserver.openaitest.controller;


import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionChatRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.TokenRequest;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.response.CompletionChatResponse;
import com.mobilesuit.mainserver.openaitest.dto.openai.java.response.CompletionResponse;
import com.mobilesuit.mainserver.openaitest.service.GPTChatRestService;
import com.mobilesuit.mainserver.openaitest.service.GPTChatServiceAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/api/chatgpt/rest")
@RequiredArgsConstructor
public class ChatGPTRestController {

  private final GPTChatRestService gptChatRestService;
  private final GPTChatServiceAsync gptChatServiceAsync;
  private final int MAX_ATTEMPTS = 20;
  private int count=0;
  /* 데이터 받는 형식
  {
    "model": "gpt-3.5-turbo",
    "prompt": "안녕하세요 봇에 대해 알고 싶어요",
    "maxTokens": 1000
  }
   */
  @PostMapping("/completion")
  public CompletionResponse completion(final @RequestBody GPTCompletionRequest gptCompletionRequest) {

    return gptChatRestService.completion(gptCompletionRequest);
  }
  /* 데이터 받는 형식
  {
    "model": "gpt-3.5-turbo",
    "role": "user",
    "message": "안녕하세요 봇에 대해 알고 싶어요",
    "maxTokens": 1000
  }
   */
  @PostMapping("/completion/chat")
  public CompletionChatResponse completionChat(final @RequestBody GPTCompletionChatRequest gptCompletionChatRequest) {

    return gptChatRestService.completionChat(gptCompletionChatRequest);
  }

  // 4개의 모델에 답변 제출하라고 요청
  @PostMapping("/completion/chat/string")
  public List<String> completionChatBroadCast(@RequestParam String message) {

    return gptChatRestService.completionChatBroadCast(message);
  }

  @PostMapping("/completion/chat/string2")
  public HashMap<String,CompletionChatResponse> completionChatCommentAndCodeReview(@RequestBody String message) {
    log.info("도착했습니다");
    return gptChatRestService.singleCompletionCommentAndCodeReview(message);
  }

  @PostMapping("/completion/chat/prompt/create")
  public CompletionChatResponse codeReviewResponse(@RequestBody String message) {

    log.info("도착했습니다 테스트");
    return gptChatRestService.promptTest(message);
  }

  @PostMapping("/completion/chat/prompt/user/token")
  public CompletionChatResponse codeReviewResponseUserToken(@RequestBody TokenRequest tokenRequest) {

    log.info("도착했습니다 테스트");
    return gptChatRestService.promptUserToken(tokenRequest);
  }

  @PostMapping("/completion/chat/prompt/line/comment")
  public CompletionChatResponse codeReviewLineComment(@RequestBody TokenRequest tokenRequest) {

    log.info("도착했습니다 라인 코멘트");
    return gptChatRestService.promptLineComment(tokenRequest);
  }


  @PostMapping("/completion/chat/prompt/async")
  public CompletableFuture<HashMap<String, CompletionChatResponse>> codeReviewLineCommentAsync(@RequestBody TokenRequest tokenRequest) {

    log.info("도착했습니다 라인 코멘트");
    log.info("토큰 "+tokenRequest.getToken());
//    CompletableFuture<CompletionChatResponse> commentFuture = gptChatServiceAsync.promptLineCommentAsync(tokenRequest);
//    CompletableFuture<CompletionChatResponse> cleanCodeFuture = gptChatServiceAsync.promptCleanCodeAsync(tokenRequest);
//    CompletableFuture<CompletionChatResponse> codeReviewFuture = gptChatServiceAsync.promptCodeReviewAsync(tokenRequest);
//    CompletableFuture<CompletionChatResponse> commitMessageFuture = gptChatServiceAsync.promptCommitMessageAsync(tokenRequest);
//
//    return CompletableFuture.allOf(commentFuture, cleanCodeFuture, codeReviewFuture, commitMessageFuture)
//            .thenApply(v -> {
//              HashMap<String, CompletionChatResponse> response = new HashMap<>();
//              try {
//                response.put("comment", commentFuture.get());
//                response.put("cleanCode", cleanCodeFuture.get());
//                response.put("codeReview", codeReviewFuture.get());
//                response.put("commitMessage", commitMessageFuture.get());
//              } catch (InterruptedException | ExecutionException e) {
//                // 예외 처리
//              }
//              return response;
//            });
    return attemptAsyncCalls(tokenRequest, count)
            .thenApply(response -> {
              if (response.containsValue(null)) {
                // 모든 값을 다시 시도
                count++;
                return codeReviewLineCommentAsync(tokenRequest).join();

              } else {
                count=0;
                return response;
              }
            });
  }//codeReviewLineComment end

  private CompletableFuture<HashMap<String, CompletionChatResponse>> attemptAsyncCalls(TokenRequest tokenRequest, int attempt) {
    if (attempt > MAX_ATTEMPTS) {
      throw new RuntimeException("최대 재시도 횟수 초과");
    }

//    CompletableFuture<CompletionChatResponse> commentFuture = retryAsync(gptChatServiceAsync::promptLineCommentAsync, tokenRequest);
//    CompletableFuture<CompletionChatResponse> cleanCodeFuture = retryAsync(gptChatServiceAsync::promptCleanCodeAsync, tokenRequest);
//    CompletableFuture<CompletionChatResponse> codeReviewFuture = retryAsync(gptChatServiceAsync::promptCodeReviewAsync, tokenRequest);
//    CompletableFuture<CompletionChatResponse> commitMessageFuture = retryAsync(gptChatServiceAsync::promptCommitMessageAsync, tokenRequest);

    CompletableFuture<CompletionChatResponse> commentFuture = gptChatServiceAsync.promptLineCommentAsync(tokenRequest);
    CompletableFuture<CompletionChatResponse> cleanCodeFuture = gptChatServiceAsync.promptCleanCodeAsync(tokenRequest);
    CompletableFuture<CompletionChatResponse> codeReviewFuture = gptChatServiceAsync.promptCodeReviewAsync(tokenRequest);
    CompletableFuture<CompletionChatResponse> commitMessageFuture = gptChatServiceAsync.promptCommitMessageAsync(tokenRequest);

    return CompletableFuture.allOf(commentFuture, cleanCodeFuture, codeReviewFuture, commitMessageFuture)
            .thenApply(v -> {
              HashMap<String, CompletionChatResponse> response = new HashMap<>();
              try {
                response.put("comment", commentFuture.get());
                response.put("cleanCode", cleanCodeFuture.get());
                response.put("codeReview", codeReviewFuture.get());
                response.put("commitMessage", commitMessageFuture.get());
              } catch (InterruptedException | ExecutionException e) {
                // 예외 처리
              }
              return response;
            });
  }

  private CompletableFuture<CompletionChatResponse> retryAsync(Function<TokenRequest, CompletableFuture<CompletionChatResponse>> asyncFunction, TokenRequest tokenRequest) {
    return asyncFunction.apply(tokenRequest)
            .exceptionally(e -> {
              // 여기에서 재시도 로직을 구현할 수 있습니다.
              // 예: 일정 시간 대기 후 재시도
              return null;
            });
  }
}
