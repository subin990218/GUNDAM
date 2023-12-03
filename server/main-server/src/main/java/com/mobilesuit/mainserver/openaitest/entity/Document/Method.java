package com.mobilesuit.mainserver.openaitest.entity.Document;

import com.mobilesuit.mainserver.openaitest.dto.documentation.DocDto;
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
@Table(name = "method")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Method {

    //메소드의 기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Integer methodId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "return_type", length = 20, nullable = false)
    private String returnType;

    @Column(name = "doc_id") // 외래 키 이름 지정
    private int doc_id;

    public MethodDto MethodToDto(List<TypeSetDto> paramList){
        return MethodDto.builder()
                .name(name)
                .returnType(returnType)
                .paramList(paramList)
                .build();

    }
}
