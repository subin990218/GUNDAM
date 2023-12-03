package com.mobilesuit.mainserver.openaitest.controller;

import com.mobilesuit.mainserver.openaitest.dto.documentation.DocDto;
import com.mobilesuit.mainserver.openaitest.dto.documentation.DocInfo;
import com.mobilesuit.mainserver.openaitest.dto.documentation.RecentDocDto;
import com.mobilesuit.mainserver.openaitest.dto.documentation.TypeSetDto;
import com.mobilesuit.mainserver.openaitest.dto.gitdata.UserInfo;
import com.mobilesuit.mainserver.openaitest.entity.Document.Doc;
import com.mobilesuit.mainserver.openaitest.entity.tmpDocument.TmpDoc;
import com.mobilesuit.mainserver.openaitest.service.DocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class DocController {
    @Autowired
    DocService docService;

    @GetMapping("/recent")
    public ResponseEntity<List<RecentDocDto>> getRecentFile(@RequestParam String userId, @RequestParam String repoName){
        List<RecentDocDto> recentDocDtoList = docService.getRecentDoc(userId,repoName);
        return ResponseEntity.ok().body(recentDocDtoList);
    }
    @GetMapping("/repolist")
    public ResponseEntity<List<String>> getRepoList(@RequestParam String userId) throws IOException {
        log.info("userId: "+userId);
        List<String> repoList = docService.getRepoList(userId);

        if(repoList != null){
            log.info("repo 요청 반환 값: "+repoList.toString());
            log.info("repo 정상 반환");
            return ResponseEntity.ok().body(repoList);
        }else{
            log.info("repo를 찾지 못함");
            return ResponseEntity.notFound().build();
        }

    }
    @PostMapping("/save")
    public ResponseEntity<Integer> getClassDoc( @RequestPart("part1") DocDto docDto, @RequestPart("part2") List<TypeSetDto> MPList){

        System.out.println("doc-----");
        docDto.setMPList(MPList);
        System.out.println(docDto.toString());
        int res =0;
        if(docDto.getType().equals("class")){
            res = docService.saveClassDoc(docDto);
        }else{
            res = docService.saveMethodDoc(docDto);
        }
        if(res == 1){
            return ResponseEntity.ok().body(1);
        }else{
            return ResponseEntity.badRequest().body(0);
        }

    }

    @GetMapping("/doclist")
    public ResponseEntity<List<DocInfo>> getDocList(@RequestParam String userId,@RequestParam String repoName){
        // 날짜와 파일경로를 내려준다.

        System.out.println("doclist");
        List<DocInfo> docInfoList = docService.getInfoList(userId,repoName);
        return ResponseEntity.ok().body(docInfoList);

    }

    @GetMapping("/detail")
    public ResponseEntity<DocDto> getDoc(@RequestParam int docId){
        // 날짜와 파일경로를 내려준다.
        DocDto docDto = docService.getDoc(docId);
        return ResponseEntity.ok().body(docDto);

    }

    @PostMapping("/document")
    public String getContent(final @RequestBody String content) {
        System.out.println("으어 ... ");
        System.out.println(content); //json



        String filePath = docService.storeToServer(content);
        if(filePath == null){
            System.out.println("파일이 저장에 문제가 생겼을때 처리필요");
        }

        System.out.println("결과 : " + filePath);
        TmpDoc tmpDoc = TmpDoc.builder()
                .filePath(filePath)
                .build();
        return null;
    }
//    @GetMapping("/document")
//    public ResponseEntity<String> getDocAddress(@RequestParam("test") String docAddress){
//        //docAddress : 여기에 어느 레포지토리의 브랜치를 원하는지 알려준다.
//
//
//        System.out.println("DFG");
//        String html = docService.getDocAddress(docAddress);
////        System.out.println("getDocAddress : " + docAddress);
////        String serverAddress = "http://192.168.30.141/D:/E207/CodeDocument/hohoho.md";
////        TmpDocDto address = new TmpDocDto(0,serverAddress);
//
//        return ResponseEntity.ok().body(html);
//    }

}
