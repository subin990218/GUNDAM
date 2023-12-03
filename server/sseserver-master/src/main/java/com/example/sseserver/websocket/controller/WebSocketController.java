package com.example.sseserver.websocket.controller;

import com.example.sseserver.websocket.dto.WebSocketDto;
import com.example.sseserver.websocket.handler.ChannelHandler;
import com.example.sseserver.websocket.handler.ChannelWebSocketHandler;
import com.example.sseserver.websocket.handler.ReactiveWebSocketHandler;
import com.example.sseserver.websocket.session.SessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/socket", method = {RequestMethod.POST, RequestMethod.GET})
//@CrossOrigin("https://k9e207.p.ssafy.io/")
@CrossOrigin("*")
public class WebSocketController {
    private final ReactiveWebSocketHandler webSocketHandler;

    //private final ChannelWebSocketHandler channelWebSocketHandler;
    private final ChannelHandler channelHandler;

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("hi");
    }

    @PostMapping("/send-message")
    public Mono<Void> sendMessage(@RequestBody String message) {
        webSocketHandler.sendToAllSessions(message);
        return Mono.empty();
    }

    @GetMapping("/status/{user-name}/{repository-name}")
    public Mono<ResponseEntity<List<SessionInfo>>> sendStatusOfChannel(
            @PathVariable(name = "repository-name") String repositoryName,
            @PathVariable(name = "user-name")String userName){

        String endPoint = userName+"/"+repositoryName;
        log.info("end Point: "+endPoint);

        List<SessionInfo> userInfoList = new ArrayList<>();
        try {
            channelHandler.getHandlerFromChannel(endPoint)
                    .getSessionInfoMap().forEach((k, v) -> userInfoList.add(v));
        }catch (NullPointerException e){
            return Mono.just(ResponseEntity.ok().body(new ArrayList<>()));
        }
        return Mono.just(ResponseEntity.ok().body(userInfoList));
    }
}
