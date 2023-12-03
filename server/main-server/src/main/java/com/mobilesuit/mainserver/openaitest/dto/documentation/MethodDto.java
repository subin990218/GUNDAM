package com.mobilesuit.mainserver.openaitest.dto.documentation;

import com.mobilesuit.mainserver.openaitest.entity.Document.Doc;
import com.mobilesuit.mainserver.openaitest.entity.Document.Method;
import lombok.*;

import java.util.List;
@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
@Builder
public class MethodDto {
    String name;
    String returnType;

    List<TypeSetDto> paramList;

    public Method toEntity(int docId) { //!!! Dto의 메소드로  CardResponseDto  card, card.

        return Method.builder()
                .doc_id(docId)
                .name(name)
                .returnType(returnType)
                .build();

    }
}
