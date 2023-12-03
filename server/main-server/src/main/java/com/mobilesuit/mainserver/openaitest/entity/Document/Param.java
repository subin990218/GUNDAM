package com.mobilesuit.mainserver.openaitest.entity.Document;

import com.mobilesuit.mainserver.openaitest.dto.documentation.TypeSetDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "param")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Param { // 클래스 문서의 각 메서드의 파라미터 값만을 저장
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", length = 100, nullable = false)
    private String type;

    @Column(name = "variable", length = 100, nullable = false)
    private String variable;


    @Column(name = "method_id") // 외래 키 이름 지정
    private int methodId;

    public TypeSetDto ParamToDto(){
        return TypeSetDto.builder()
                .type(type)
                .variable(variable)
                .build();

    }
}
