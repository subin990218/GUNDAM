package com.mobilesuit.clientplugin.documentation.dto;

import lombok.*;

import java.util.List;
@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
public class Method {
    String name;
    String returnType;

    List<TypeSet> paramList;
}
