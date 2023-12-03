package com.mobilesuit.clientplugin.gpt.repository;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ResponseRepository {
    private static ResponseRepository instance;

    //전체 답변 저장하는 리스트 개선 필요
    private List<String> responseList;
    // 답변이 2개로 와야 하는 경우 매핑을 어떻게 할건지 경주님이랑 논의 필요함
    private List<CodeInfo> codeInfoList;
    // 이따구로 짜면 한번 코드 리뷰 보내고 체크 해제하고 코드 리뷰 다시 보내면 이전꺼 까지 같이 보내지 않나?

    private Map<String, String> codeCommentsMap;
    private Map<String, String> codeReviewMap;
    private Map<String, String> cleanCodeMap;
    private Map<String, String> commitMessageMap;

    private Map<String, CodeInfo> codeInfoMap;
    private Map<String, String> responseMap;
    private Map<String, String>[] listMap;

    private ResponseRepository(){

        //전체 답변 저장하는 리스트 개선 필요
        responseList = new ArrayList<>();
        codeInfoList = new ArrayList<>();

        codeCommentsMap = new HashMap<>();
        codeReviewMap =  new HashMap<>();
        cleanCodeMap = new HashMap<>();
        commitMessageMap =  new HashMap<>();
        responseMap =  new HashMap<>();
        codeInfoMap =  new HashMap<>();

        listMap = new HashMap[4];
        listMap[0] = codeCommentsMap;
        listMap[1] = codeReviewMap;
        listMap[2] = cleanCodeMap;
        listMap[3] = commitMessageMap;
    }

    public static ResponseRepository getInstance() {
        if (instance == null) {
            instance = new ResponseRepository();
        }
        return instance;
    }


    public void cleanMap(){
        codeInfoList.clear();
        codeCommentsMap.clear();
        codeReviewMap.clear();
        //전체 답변 저장하는 리스트 개선 필요
        responseMap.clear();
        codeInfoMap.clear();
        cleanCodeMap.clear();
        commitMessageMap.clear();
    }
}//class ResponseRepository end
