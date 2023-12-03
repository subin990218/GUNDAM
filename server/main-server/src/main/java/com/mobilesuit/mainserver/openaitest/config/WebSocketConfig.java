package com.mobilesuit.mainserver.openaitest.config;


import com.mobilesuit.mainserver.openaitest.handler.CodeReviweHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


//registry.addHandler(streamCompletionHandler, "/chat/stream").setAllowedOrigins("*");
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Value("${TOKEN}")
  //@Value("${gpt.token}")
  private String token;
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new CodeReviweHandler(), "/codeReviewHandler")
            .setAllowedOrigins("*"); // 모든 도메인에서의 웹소켓 연결을 허용. 실제 환경에서는 특정 도메인만 허용하도록 설정하는 것이 좋습니다.
  }



}