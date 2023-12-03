package com.mobilesuit.mainserver.openaitest.entity.Document;

import com.mobilesuit.mainserver.openaitest.common.BaseEntity;
import com.mobilesuit.mainserver.openaitest.dto.documentation.DocDto;
import com.mobilesuit.mainserver.openaitest.dto.documentation.MethodDto;
import com.mobilesuit.mainserver.openaitest.dto.documentation.TypeSetDto;
import com.mobilesuit.mainserver.openaitest.dto.tmpdoc.TmpDocDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Table(name = "doc")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doc{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Integer docId;

    @Column(name = "repo_name", length = 150, nullable = false)
    private String repoName;
    @Column(name = "user_id", length = 100, nullable = false)
    private String userId;

    @Column(name = "file_path", length = 150, nullable = false)
    private String filePath;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "type", length = 10, nullable = false)
    private String type;

    @Column(name = "code_review", columnDefinition = "LONGTEXT", nullable = true)
    private String codeReview;

    @Column(name = "code", columnDefinition = "LONGTEXT", nullable = true)
    private String code;

    @Column(name = "clean_code", columnDefinition = "LONGTEXT", nullable = true)
    private String cleanCode;

    @Column(name = "return_type", length = 20, nullable = true)
    private String returnType;

    @Column(nullable = false)
    private LocalDate createdDate;

    public DocDto DocToDto(List<MethodDto> methodList, List<TypeSetDto> MemberOrParamList){
        return DocDto.builder()
                .repoName(repoName)
                .userId(userId)
                .filePath(filePath)
                .name(name)
                .type(type)
                .codeReview(codeReview)
                .code(code)
                .cleanCode(cleanCode)
                .returnType(returnType)
                .methodList(methodList)
                .MPList(MemberOrParamList)
                .id(docId)
                .build();

    }

    public void setDate(LocalDate date){
        createdDate = date;
    }
}
