package com.mobilesuit.mainserver.openaitest.dto.tmpdoc;

import com.mobilesuit.mainserver.openaitest.entity.tmpDocument.TmpDoc;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class TmpDocDto {

    int id;
    String filePath;

    public TmpDoc toEntity() { //!!! Dto의 메소드로  CardResponseDto  card, card.

        return TmpDoc.builder()
                .filePath(filePath)
                .build();

    }

    @Builder
    public TmpDocDto(int id, String filePath){
        this.id = id;
        this.filePath = filePath;
    }

}
