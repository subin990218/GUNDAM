package com.mobilesuit.mainserver.openaitest.dto.documentation;

import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
@Builder
public class RecentDocDto {
    String name; //파일의 이름
    int docId;
}
