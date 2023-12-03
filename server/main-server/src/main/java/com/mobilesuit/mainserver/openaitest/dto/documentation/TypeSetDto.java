package com.mobilesuit.mainserver.openaitest.dto.documentation;

import com.mobilesuit.mainserver.openaitest.entity.Document.Method;
import com.mobilesuit.mainserver.openaitest.entity.Document.Param;
import com.mobilesuit.mainserver.openaitest.entity.Document.Param2;
import com.mobilesuit.mainserver.openaitest.entity.Document.TypeSet;
import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
@Builder
public class TypeSetDto {
    String type;
    String variable;
    int id;

    public TypeSet toTypeSetEntity(int id) { //!!! Dto의 메소드로  CardResponseDto  card, card.

        return TypeSet.builder()
                .type(type)
                .variable(variable)
                .docId(id)
                .build();

    }

    public Param toParamEntity(int id) { //!!! Dto의 메소드로  CardResponseDto  card, card.

        return Param.builder()
                .type(type)
                .variable(variable)
                .methodId(id)
                .build();

    }
    public Param2 toParam2Entity(int id) { //!!! Dto의 메소드로  CardResponseDto  card, card.

        return Param2.builder()
                .type(type)
                .variable(variable)
                .docId(id)
                .build();

    }
}
