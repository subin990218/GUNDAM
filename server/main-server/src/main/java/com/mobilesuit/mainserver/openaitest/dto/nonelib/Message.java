package com.mobilesuit.mainserver.openaitest.dto.nonelib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String role;
    private String content;

    // constructor, getters and setters
}
