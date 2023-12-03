package com.mobilesuit.mainserver.openaitest.controller;
import com.mobilesuit.mainserver.openaitest.dto.gitdata.RepoInfo;
import com.mobilesuit.mainserver.openaitest.dto.gitdata.UserInfo;
import com.mobilesuit.mainserver.openaitest.service.GitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
//@CrossOrigin("*")
//@CrossOrigin(originPatterns ="http://192.168.30.220:3031/*",allowedHeaders = {"Authorization"}, allowCredentials = "true")
public class GitDataController {

    @Autowired
    GitService gitService;


//    @PostMapping("/git/repolist")
//    public ResponseEntity<List<String>> getRepoList(@RequestBody UserInfo userInfo) throws IOException {
//        //request.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
//        //response.setHeader("Access-Control-Allow-Origin", clientOrigin);
////        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
////        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
////        response.setHeader("Access-Control-Allow-Credentials", "true");
////        UserInfo userInfo =new UserInfo();
////
////        userInfo.setName(id);
////        //userInfo.setToken(response.getHeader("Authorization"));
////        userInfo.setToken(token);
//        List<String> repoList = gitService.getRepoList(userInfo);
//
//        if(repoList != null){
//            log.info("repo 정상 반환");
//            return ResponseEntity.ok().body(repoList);
//        }else{
//            log.info("repo를 찾지 못함");
//            return ResponseEntity.notFound().build();
//        }
//
//    }
    @PostMapping("/git/repoinfo")
    public ResponseEntity<RepoInfo> getRepoInfo(@RequestBody UserInfo userInfo,@RequestParam String repository){ //  사용자의 리포지토리 목록을 넘긴다.

//        UserInfo userInfo =new UserInfo();
//
//        userInfo.setName(id);
//        userInfo.setToken(token);
        //토큰과 아이디를 넘겨받는다.
        System.out.println(userInfo.getName() + " : " + userInfo.getToken());
        RepoInfo repoInfo = gitService.getRepoInfoList(userInfo,repository);

        if(repoInfo != null){
            log.info("repo 정상 반환");
            return ResponseEntity.ok().body(repoInfo);
        }else{
            log.info("repo를 찾지 못함");
            return ResponseEntity.notFound().build();
        }

    }
//    @PostMapping("/git/repoinfolist")
//    public ResponseEntity<List<RepoInfo>> getRepoInfoList(@RequestBody UserInfo userInfo){ //  사용자의 리포지토리 목록을 넘긴다.
//
//        //토큰과 아이디를 넘겨받는다.
//        System.out.println(userInfo.getName() + " : " + userInfo.getToken());
//        List<RepoInfo> repoInfoListList = gitService.getRepoInfoList(userInfo);
//
//        if(repoInfoListList != null){
//            log.info("repo 정상 반환");
//            return ResponseEntity.ok().body(repoInfoListList);
//        }else{
//            log.info("repo를 찾지 못함");
//            return ResponseEntity.notFound().build();
//        }
//
//    }

    @GetMapping("/git/tmpcommit")
    public ResponseEntity<RepoInfo> commitData() throws IOException {

        //https://api.github.com/repos/qlcid/Homedong/stats/code_frequency
        //String apiUrl = "https://api.github.com/repos/qlcid/Homedong/stats/code_frequency";
        String apiUrl = "https://api.github.com/repos/Zookim/MetaMong/stats/participation";
        //String apiUrl = "https://api.github.com/repos/qlcid/Homedong/stats/code_frequency";
        String oauthToken = "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";
        GitHub gitHub = new GitHubBuilder().withOAuthToken(oauthToken).build();

        // repository 할당

        GHRepository repository = gitHub.getRepository("Zookim/MetaMong");


        String repositoryName = repository.getName();
        String repositoryDescription = repository.getDescription();
        int starCount = repository.getStargazersCount();
        int forkCount = repository.getForks();
        String language = repository.getLanguage();

        System.out.println("Repository Name: " + repositoryName);
        System.out.println("Repository Description: " + repositoryDescription);
        System.out.println("Star Count: " + starCount);
        System.out.println("Fork Count: " + forkCount);
        System.out.println("Language: " + language);

        //

        List<GHCommit> commits = repository.listCommits().toList();
        System.out.println("commit size : "+commits.size());

        for (GHCommit commit : commits) {
            System.out.println("SHA: " + commit.getSHA1());
            System.out.println("Author: " + commit.getAuthor().getLogin());
            System.out.println("Message: " + commit.getCommitShortInfo().getMessage());
            System.out.println();
        }
        return null;
    }
//        repository.getBranch("master");
//        Map<String , GHBranch> test = repository.getBranches();
//
//        test.forEach((a,b)->{
//            System.out.println("ddd : "+ a.toString()+" bbb : " + b.getName());
//        });



}
