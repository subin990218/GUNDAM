package com.mobilesuit.clientplugin.documentation.dto;

import lombok.*;

import java.util.List;
@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
public class Doc {
    int id;
    //멤버 or paramList
    List<TypeSet> MPList; //MemberOrParamList
    String repoName;
    String userId;
    String filePath;
    String name;
    String type; // class or method
    String codeReview;
    String code;
    String cleanCode;
    String returnType;

    //메소드리스트
    List<Method> methodList;


}
