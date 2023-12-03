package com.mobilesuit.mainserver.openaitest.dto.documentation;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
@Builder
public class DocInfo {
    LocalDate createdDate;
    String filePath;
    int docId;
}
