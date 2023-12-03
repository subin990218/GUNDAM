package com.mobilesuit.mainserver.openaitest.service;

import com.mobilesuit.mainserver.openaitest.dto.gitdata.MostCommit;
import com.mobilesuit.mainserver.openaitest.dto.gitdata.RepoInfo;
import com.mobilesuit.mainserver.openaitest.dto.gitdata.UserInfo;
import com.mobilesuit.mainserver.openaitest.entity.Document.Doc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitService {
    public List<String> getRepoList(UserInfo userInfo) throws IOException {
        List<String> repoList = new ArrayList<>();

        GitHub github = new GitHubBuilder().withOAuthToken(userInfo.getToken()).build();
        GHRepositorySearchBuilder searchBuilder = github.searchRepositories().user(userInfo.getName());
        for (GHRepository repo : searchBuilder.list()){
            repoList.add(repo.getName());
        }
        return repoList;
    }

    public RepoInfo getRepoInfoList(UserInfo userInfo,String repoName){
        RepoInfo repoInfo = new RepoInfo();
        try {
            // GitHub API에 연결
            //GitHub github = GitHub.connect(userInfo.getId(), userInfo.getToken());
            GitHub github = new GitHubBuilder().withOAuthToken(userInfo.getToken()).build();
            //System.out.println(repoName);
            GHRepository repo = github.getRepository(userInfo.getName()+"/"+repoName);

            //일주일간의 기록읽어오기
            PagedIterable<GHCommit> commits = getWeekCommit(repo);
            System.out.println(repo.getName());
            int mostCommitsHour = getRepoCommitHour(repo); // 커밋이 가장 많은 시간대
            int commitCount = commits.toList().size(); // 일주일간의 커밋수
            MostCommit commitUser = getRepoCommitUser(commits);


            repoInfo.setRepoName(repo.getName());
            repoInfo.setMostCommitsHour(mostCommitsHour);
            repoInfo.setCommitCount(commitCount);
            repoInfo.setMostCommit(commitUser);
            System.out.println(repoInfo.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return repoInfo;
    }


//    public List<RepoInfo> getRepoInfoList(UserInfo userInfo){
//        List<RepoInfo> repoInfoList = new ArrayList<>();
//        try {
//            // GitHub API에 연결
//            //GitHub github = GitHub.connect(userInfo.getId(), userInfo.getToken());
//            GitHub github = new GitHubBuilder().withOAuthToken(userInfo.getToken()).build();
//
//            // 사용자 이름
////            String username = github.getMyself().getLogin();
////            System.out.println("name : " + username);
//
//
//
//            // 사용자의 리포지토리 목록 가져오기
//            GHRepositorySearchBuilder searchBuilder = github.searchRepositories().user(userInfo.getName());
//            for (GHRepository repo : searchBuilder.list()) {
//
//                //일주일간의 기록읽어오기
//                PagedIterable<GHCommit> commits = getWeekCommit(repo);
//                System.out.println(repo.getName());
//                int mostCommitsHour = getRepoCommitHour(repo); // 커밋이 가장 많은 시간대
//                int commitCount = commits.toList().size(); // 일주일간의 커밋수
//                MostCommit commitUser = getRepoCommitUser(commits);
//
//                RepoInfo repoInfo = new RepoInfo();
//                repoInfo.setRepoName(repo.getName());
//                repoInfo.setMostCommitsHour(mostCommitsHour);
//                repoInfo.setCommitCount(commitCount);
//                repoInfo.setMostCommit(commitUser);
//
//                repoInfoList.add(repoInfo);
//
//                System.out.println(repoInfo.toString());
//                //if(repoInfoList.size() ==1){
//                //    break;
//                // }
//                // 각레포별로 보내준다.
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return repoInfoList;
//    }
    public PagedIterable<GHCommit> getWeekCommit(GHRepository repo){
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date oneWeekAgo = calendar.getTime();

        PagedIterable<GHCommit> commits = repo.queryCommits()
                .since(oneWeekAgo) // 일주일 전부터
                .until(now) // 현재까지의 커밋을 가져옴
                .list();

        return commits;
    }
    public MostCommit getRepoCommitUser(PagedIterable<GHCommit> commits) throws IOException {
        // 해당 레포에 가장 많이 커밋한 사람
        //최근 일주일간만
        MostCommit commitUser = new MostCommit();

        // 커밋별로 커미터(커밋을 한 사람)를 카운트하는 맵 생성
        Map<String, Integer> committerCountMap = new HashMap<>();

        for (GHCommit commit : commits) {
            GHUser committer = commit.getCommitter();
            if(committer == null){
                int count = committerCountMap.getOrDefault("etc", 0);
                committerCountMap.put("etc", count + 1);
                continue;
            }
            //System.out.println("committer : " + committer);
            String committerName = committer.getLogin();
            int count = committerCountMap.getOrDefault(committerName, 0);
            committerCountMap.put(committerName, count + 1);
        }


        // 가장 많은 커밋을 한 사람 찾기
        String mostProlificCommitter = null;
        int maxCommitCount = 0;

        for (Map.Entry<String, Integer> entry : committerCountMap.entrySet()) {
            if (entry.getValue() > maxCommitCount) {
                mostProlificCommitter = entry.getKey();
                maxCommitCount = entry.getValue();
            }
        }
        commitUser.setMostCommitUser(mostProlificCommitter);
        commitUser.setMostCommitUserNum(maxCommitCount);

        return commitUser;
    }

    public int getRepoCommitHour(GHRepository repo) throws IOException { // 커밋이 가장 많은 시간대
        PagedIterable<GHCommit> commits = repo.listCommits();

        int mostCommitsHour = -1;
        // 시간대별로 커밋 횟수를 저장할 맵 생성
        Map<Integer, Integer> commitCountsByHour = new HashMap<>();

        // 커밋 이력 순회
        for (GHCommit commit : commits) {
            Date commitDate = commit.getCommitDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(commitDate); // 그날에, 그시간

            int hour = calendar.get(Calendar.HOUR_OF_DAY);// 그시간을 24시로 알려준다.
            int commitCount = commitCountsByHour.getOrDefault(hour, 0); //시간별 결과값?
            commitCountsByHour.put(hour, commitCount + 1);
        }

        // 가장 많은 커밋이 발생한 시간대 찾기

        int mostCommitsCount = -1;

        for (Map.Entry<Integer, Integer> entry : commitCountsByHour.entrySet()) {
            if (entry.getValue() > mostCommitsCount) {
                mostCommitsHour = entry.getKey();
                mostCommitsCount = entry.getValue();
            }
        }
        return mostCommitsHour;
    }


}
