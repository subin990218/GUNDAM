package com.mobilesuit.mainserver.openaitest.entity.tmpDocument;

import com.mobilesuit.mainserver.openaitest.common.BaseEntity;
import com.mobilesuit.mainserver.openaitest.dto.tmpdoc.TmpDocDto;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

@Entity
@Getter
@Table(name = "tmp_doc")
@AllArgsConstructor
@NoArgsConstructor

public class TmpDoc extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;


    @Builder
    public TmpDoc(String filePath){
        this.filePath = filePath;
    }

    public TmpDocDto TmpDocToDto(){
        return TmpDocDto.builder()
                .id(id)
                .filePath(filePath)
                .build();

    }
}




