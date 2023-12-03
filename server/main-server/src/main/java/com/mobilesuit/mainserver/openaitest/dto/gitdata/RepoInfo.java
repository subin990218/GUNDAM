package com.mobilesuit.mainserver.openaitest.dto.gitdata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@NoArgsConstructor
@ToString
public class RepoInfo {

    String repoName;
    int mostCommitsHour; // 커밋이 가장 많은 시간대
    int commitCount;
    MostCommit mostCommit;


}


