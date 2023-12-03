package com.mobilesuit.mainserver.openaitest.dto.documentation;

import com.mobilesuit.mainserver.openaitest.entity.Document.Doc;
import com.mobilesuit.mainserver.openaitest.entity.tmpDocument.TmpDoc;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
@Builder
public class DocDto {
    int id;
    List<TypeSetDto> MPList;
    String repoName;
    String userId;
    String filePath;
    String name; // 파일의 이름
    String type; // class or method
    String codeReview;
    String code;
    String cleanCode;
    String returnType;
    //메소드리스트
    List<MethodDto> methodList;
    //멤버 or paramList



    public Doc toEntity() { //!!! Dto의 메소드로  CardResponseDto  card, card.

        return Doc.builder()
                .repoName(repoName)
                .userId(userId)
                .filePath(filePath)
                .name(name)
                .type(type)
                .codeReview(codeReview)
                .code(code)
                .cleanCode(cleanCode)
                .returnType(returnType)
                .build();

    }



}
