package com.example.sseserver.sse.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class SseController {

    private final Map<String, Map<String, FluxSink<String>>> repos = new ConcurrentHashMap<>();


    @GetMapping(value = "/stream-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents(@RequestHeader(name = "repository") String repository) {



        if(!repos.containsKey(repository)) {
            repos.put(repository,new ConcurrentHashMap<>());
        }
        Map<String,FluxSink<String>> clients = repos.get(repository);
        return Flux.create(sink -> {
            String uuid = UUID.randomUUID().toString();

            clients.put(uuid, sink); // client's ID rather than UUID
            log.info("Requested on "+ uuid);
            sink.onCancel(() -> {
                clients.remove(uuid);
                log.info("Client disconnected: " + uuid);
            });
            sink.next(uuid+" : Connected to " + repository);
        });
    }

    @GetMapping(value = "/stream-event")
    public void onEvent(@RequestHeader(name = "repository") String repository,@RequestBody String message) {
        Map<String,FluxSink<String>> clients = repos.get(repository);
        for (FluxSink<String> client : clients.values()) {
            try{
                client.next(message);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}