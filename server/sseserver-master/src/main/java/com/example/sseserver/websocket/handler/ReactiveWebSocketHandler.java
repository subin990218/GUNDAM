package com.example.sseserver.websocket.handler;

import com.example.sseserver.websocket.dto.WebSocketDto;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {


    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        this.sessions.add(session);
        System.out.println(sessions.size());
        session.getHandshakeInfo().getHeaders().forEach((k,v)-> {
            System.out.println(k+":"+v);
        });
        Gson gson = new Gson();

        return session.receive()
                //.publishOn(Schedulers.boundedElastic())
                .doOnNext(message -> {
                    System.out.println(message.getType());
                    System.out.println("Received message: " + message.getPayloadAsText());
                    WebSocketDto.Event event = gson.fromJson(message.getPayloadAsText(),WebSocketDto.Event.class);
                    System.out.println("Event : "+event);
                    if(event.getCode().equals("USER")) {
                        for (WebSocketSession webSocketSession : this.sessions) {
                            if(webSocketSession.isOpen()) {
                                webSocketSession.send(Mono.just(webSocketSession.textMessage(message.getPayloadAsText()))).subscribe();
                            }
                        }
                    }
                })
                .doOnError(o->{
                    o.printStackTrace();
                    System.out.println("오류 나서 세션 지워야하는데?");
                    this.sessions.remove(session);
                })
                .doFinally(signalType -> {
                    if (SignalType.ON_COMPLETE.equals(signalType)) {
                        this.sessions.remove(session);
                    }
                })
                .then();
    }

    public void sendToAllSessions(String message){
        sessions.forEach(session->{session.send(Mono.just(session.textMessage((message)))).subscribe();});
    }
}
