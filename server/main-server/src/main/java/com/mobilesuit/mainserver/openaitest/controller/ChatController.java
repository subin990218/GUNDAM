package com.mobilesuit.mainserver.openaitest.controller;

import com.mobilesuit.mainserver.openaitest.dto.nonelib.ChatRequest;
import com.mobilesuit.mainserver.openaitest.dto.nonelib.ChatResponse;
import com.mobilesuit.mainserver.openaitest.service.ReflectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// not use openai java, servce only nonelib package use

@RestController
public class ChatController {

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    private ReflectionService reflectionService = new ReflectionService();
    @GetMapping("/reflection/test")
    public void test(){
        reflectionService.reflectionClass();

    }


    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        // create a request
        ChatRequest request = new ChatRequest(model, prompt);

        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }

    @PostMapping("/chatPost")
    public String chat2(@RequestParam String prompt) {
        System.out.println(prompt);
        // create a request
        ChatRequest request = new ChatRequest(model, prompt);

        System.out.println(request.getMessages().size());
        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }
        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }

    @GetMapping("/api/chat/test")
    public String chatTest(@RequestParam String prompt) {
        // create a request

        // return the first response
        return "send success";
    }

}