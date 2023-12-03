package com.mobilesuit.mainserver.openaitest.entity.Document;

import com.mobilesuit.mainserver.openaitest.dto.documentation.MethodDto;
import com.mobilesuit.mainserver.openaitest.dto.documentation.TypeSetDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "type_set")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", length = 7, nullable = false)
    private String type;

    @Column(name = "variable", length = 50, nullable = false)
    private String variable;


    @Column(name = "doc_id") // 외래 키 이름 지정
    private int docId;
    public TypeSetDto TypeSetToDto(){
        return TypeSetDto.builder()
                .type(type)
                .variable(variable)
                .build();

    }


}

