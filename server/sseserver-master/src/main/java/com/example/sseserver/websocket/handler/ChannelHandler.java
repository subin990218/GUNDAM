package com.example.sseserver.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelHandler implements WebSocketHandler {
    private final Map<String, ChannelWebSocketHandler> handlers = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String[] urlSplit = session.getHandshakeInfo().getUri().getPath().split("/");
        String channelId = urlSplit[2]+"/"+urlSplit[3];
        ChannelWebSocketHandler handler = handlers.get(channelId);
        System.out.println(channelId+" is connected!");
        if (handler == null) {
            handler = new ChannelWebSocketHandler();
            handlers.put(channelId, handler);
            System.out.println("new Channel Opened!");
        }
        return handler.handle(session);
    }

    public ChannelWebSocketHandler getHandlerFromChannel(String channel){
        return handlers.get(channel);
    }
}
