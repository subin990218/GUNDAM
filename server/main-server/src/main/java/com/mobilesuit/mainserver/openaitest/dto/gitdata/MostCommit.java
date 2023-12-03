package com.mobilesuit.mainserver.openaitest.dto.gitdata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class MostCommit {
    String mostCommitUser;
    int mostCommitUserNum;
}
