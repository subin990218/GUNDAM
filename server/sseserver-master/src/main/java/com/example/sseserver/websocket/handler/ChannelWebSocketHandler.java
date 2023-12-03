package com.example.sseserver.websocket.handler;

import com.example.sseserver.websocket.dto.WebSocketDto;
import com.example.sseserver.websocket.session.SessionInfo;
import com.google.gson.Gson;
import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChannelWebSocketHandler implements WebSocketHandler {

    //private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final Map<String, SessionInfo> sessionInfoMap = new ConcurrentHashMap<>();

    private final Map<String, String> nameToSessionId = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        this.sessions.put(session.getId(),session);
        System.out.println(sessions.size());
        session.getHandshakeInfo().getHeaders().forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
        Gson gson = new Gson();

        return session.receive()
                //.publishOn(Schedulers.boundedElastic())
                .doOnNext(message -> {
                    System.out.println(message.getType());
                    System.out.println("Received message: " + message.getPayloadAsText());
                    WebSocketDto.Event event = gson.fromJson(message.getPayloadAsText(), WebSocketDto.Event.class);
                    System.out.println("Event : " + event);
                    if (event.getCode().equals("USER")) {
                        //sessionInfoMap.get(session.getId()).setUserName(event.get);

                        WebSocketDto.UserInfo userInfo = gson.fromJson(event.getText(), WebSocketDto.UserInfo.class);
                        sessionInfoMap.get(session.getId()).setOnFile(userInfo.getTarget());

                        sendAll(message);
                    } else if (event.getCode().equals("OPEN")) {
                        if(null!=event.getText()) {
                            sessionInfoMap.put(session.getId(), new SessionInfo());
                            sessionInfoMap.get(session.getId()).setUserName(event.getText());
                            nameToSessionId.put(event.getText(), session.getId());
                            sendAll(message);
                            WebSocketDto.Event sendEvent = WebSocketDto.Event.builder()
                                    .code("STATUS").text(getSessionInfoJson()).build();
                            String sendMessage = gson.toJson(sendEvent);
                            session.send(Mono.just(session.textMessage(sendMessage))).subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();
                        }
                    } else if (event.getCode().equals("PING")) {
                        sendAll(message);
                    }else if(event.getCode().equals("PUSH")){
                        sendAll(message);
                    }
                    else if (event.getCode().equals("MSG")){
                        WebSocketDto.UserInfo messageInfo = gson.fromJson(event.getText(), WebSocketDto.UserInfo.class);
                        try {
                            String userName = messageInfo.getUserName().split(":")[1];
                            String sessionId = nameToSessionId.get(userName);

                            WebSocketSession sendTo = sessions.get(sessionId);
                            sendTo.send(Mono.just(sendTo.textMessage(message.getPayloadAsText())))
                                    .subscribeOn(Schedulers.boundedElastic()).subscribe();
                        }catch (NullPointerException e){
                            session.send(Mono.just(session.textMessage(e.getMessage())))
                                    .subscribeOn(Schedulers.boundedElastic()).subscribe();
                        }
                    }
                })
                .doOnError(o -> {
                    o.printStackTrace();
                    System.out.println("오류 나서 세션 지워야하는데?");
                    endOfSession(session);
                })
                .doFinally(signalType -> {
                    if (SignalType.ON_COMPLETE.equals(signalType)) {
                        endOfSession(session);
                    }
                })
                .then();
        /*sessions.add(session); // 클라이언트 연결 시 WebSocketSession 저장

        // 클라이언트로부터 받은 메시지 처리
        Flux<WebSocketMessage> incoming = session.receive();

        // 서버에서 클라이언트로 보내는 메시지 처리
        Mono<Void> outgoing = session.send(
                Flux.just(session.textMessage("새로운 클라이언트가 연결되었습니다."))
        );

        return incoming.doFinally(signalType -> {
            sessions.remove(session); // 클라이언트 연결 종료 시 WebSocketSession 제거
        }).thenMany(outgoing).then();*/
    }
    /*public Mono<Void> process(WebSocketSession session, String sendMessage) {
        return session.send(Mono.just(session.textMessage(sendMessage)));
    }*/

    private void sendAll(WebSocketMessage message) {
        for (WebSocketSession webSocketSession : this.sessions.values()) {
            if (webSocketSession.isOpen()) {
                webSocketSession.send(Mono.just(webSocketSession.textMessage(message.getPayloadAsText())))
                        .subscribeOn(Schedulers.boundedElastic()).subscribe();
            }
        }
    }

    private void sendAll(String message) {
        for (WebSocketSession webSocketSession : this.sessions.values()) {
            if (webSocketSession.isOpen()) {
                webSocketSession.send(Mono.just(webSocketSession.textMessage(message)))
                        .subscribeOn(Schedulers.boundedElastic()).subscribe();
            }
        }
    }

    private void endOfSession(WebSocketSession session){
        Gson gson = new Gson();
        String userName = sessionInfoMap.get(session.getId()).getUserName();
        WebSocketDto.Event event = WebSocketDto.Event.builder()
                .code("CLOSE").text(userName).build();
        String json = gson.toJson(event);
        sendAll(json);

        nameToSessionId.remove(userName);
        sessionInfoMap.remove(session.getId());
        this.sessions.remove(session);
    }

    private String getSessionInfoJson(){
        Gson gson = new Gson();

        return gson.toJson(this.sessionInfoMap.values().stream().toList());
    }

    public Map<String, SessionInfo> getSessionInfoMap() {
        return sessionInfoMap;
    }
}