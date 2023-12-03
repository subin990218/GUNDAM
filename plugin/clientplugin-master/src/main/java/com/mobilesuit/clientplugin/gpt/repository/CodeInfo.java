package com.mobilesuit.clientplugin.gpt.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Setter
@ToString
public class CodeInfo {
    private String filePath;
    private int lineNum;

    private String type;

    private String name;


}
