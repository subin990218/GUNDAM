package com.mobilesuit.mainserver.openaitest.service;

import com.mobilesuit.mainserver.openaitest.dto.openai.java.request.GPTCompletionChatRequest;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Service
public class ReflectionService {
    public void reflectionClass() {
        // Sample class 객체를 생성
        GPTCompletionChatRequest sample = new GPTCompletionChatRequest ();

        // SampleClass의 클래스 정보를 가져옴
        Class<?> clazz = sample.getClass();

        // 클래스 이름 출력
        System.out.println("Class Name: " + clazz.getName());

        // 모든 메소드 이름 출력
        System.out.println("\nMethods:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(method.getName());

            // 해당 메소드에 붙은 모든 어노테이션을 가져옵니다.
            for (Annotation annotation : method.getAnnotations()) {
                System.out.println("  Annotation: " + annotation.annotationType().getName());
            }
        }

        // 모든 필드 이름 출력
        System.out.println("\nFields:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(field.getName());
        }
    }
}