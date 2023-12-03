package com.mobilesuit.mainserver.openaitest.service;

import com.google.gson.Gson;

import com.mobilesuit.mainserver.openaitest.dto.documentation.*;
import com.mobilesuit.mainserver.openaitest.dto.tmpdoc.TmpDocDto;
import com.mobilesuit.mainserver.openaitest.entity.Document.*;
import com.mobilesuit.mainserver.openaitest.entity.tmpDocument.TmpDoc;
import com.mobilesuit.mainserver.openaitest.entity.tmpDocument.TmpDocRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocService {
    // DB에 저장하기
    // 서버에 저장하기

    @Autowired
    TmpDocRepository tmpDocRepository;
    @Autowired
    DocRepository docRepository;
    @Autowired
    MethodRepository methodRepository; //클래스의 메소드
    @Autowired
    ParamRepository paramRepository; //클래스.메소드.멤버
    @Autowired
    TypeSetRepository typeSetRepository; //멤버 필드
    @Autowired
    Param2Repository param2Repository; //메서드문서의 파라미터

    @Transactional(readOnly = false)
    public List<RecentDocDto> getRecentDoc(String userId,String repoName){
        List<RecentDocDto> recentDocDtoList = new ArrayList<>();
        LocalDate todaydate= LocalDate.now();
        System.out.println("오늘 날짜 : " + todaydate);

        List<Doc> docList = docRepository.getRecentDoc(userId,repoName,todaydate);
        docList.forEach((doc)->{
            RecentDocDto recentDocDto = new RecentDocDto();
            recentDocDto.setDocId(doc.getDocId());
            recentDocDto.setName(doc.getName());
            recentDocDtoList.add(recentDocDto);
        });

        return recentDocDtoList;
    }
    @Transactional(readOnly = false)
    public DocDto getDoc(int docId){
        DocDto docDto=null;
        Doc doc = docRepository.getReferenceById(docId);
        if(doc.getType().equals("class")){

            //멤버 필드
            List<TypeSet> typeSetList = typeSetRepository.getbyDocId(docId);
            List<TypeSetDto> typeSetDtoList = new ArrayList<>();
            if(typeSetList != null){
                for(TypeSet typeSet : typeSetList){
                    TypeSetDto typeSetDto = typeSet.TypeSetToDto();
                    typeSetDtoList.add(typeSetDto);
                }
            }

            List<Method> methodList = methodRepository.getbyDocId(docId);
            List<MethodDto> methodDtoList = new ArrayList<>();
            if(methodList != null){
                for(int i=0;i<methodList.size();i++){
                    int methodId = methodList.get(i).getDoc_id();

                    List<Param> paramList = paramRepository.getbyMehtodId(methodId);
                    List<TypeSetDto> paramDtoList = new ArrayList<>();
                    if(paramList != null){
                        for(Param param : paramList){
                            TypeSetDto typeSetDto = param.ParamToDto();
                            paramDtoList.add(typeSetDto);
                        }
                    }
                    MethodDto methodDto = methodList.get(i).MethodToDto(paramDtoList);
                    methodDtoList.add(methodDto);
                }
            }
            docDto = doc.DocToDto(methodDtoList,typeSetDtoList);

        }else{
            List<Param2> param2List = param2Repository.getbyDocId(docId);
            List<TypeSetDto> typeSetDtoList = new ArrayList<>();
            if(param2List != null){
                for(Param2 param2 : param2List){
                    TypeSetDto typeSetDto = param2.TypeSetToDto();
                    typeSetDtoList.add(typeSetDto);
                }
            }


            docDto = doc.DocToDto(null,typeSetDtoList);
        }
        return docDto;

    }
    @Transactional(readOnly = false)
    public List<DocInfo> getInfoList(String userId, String repoName){
        //id,날짜, filePath
        List<Doc> docList = docRepository.findByUserIdRepoName(userId,repoName);
        List<DocInfo> docInfoList = new ArrayList<>();
        DocInfo docInfo = new DocInfo();
        for(Doc doc : docList){

            docInfo = new DocInfo();
            docInfo.setDocId(doc.getDocId());
            docInfo.setFilePath(doc.getFilePath());
            docInfo.setCreatedDate(doc.getCreatedDate());
            docInfoList.add(docInfo);
        }

        return docInfoList;
    }
    @Transactional(readOnly = false)
    public int saveClassDoc(DocDto docdto){

        Doc doc = docdto.toEntity();
        doc.setDate(LocalDate.now());
        try {
            doc = docRepository.save(doc);

            System.out.println("id : " + doc.getDocId());

            //멤버 필드
            List<TypeSetDto> memberFieldList = docdto.getMPList();
            if (memberFieldList != null) {
                for (TypeSetDto member : memberFieldList) {
                    System.out.println("member : ");
                    System.out.println(member.toString());
                    TypeSet typeSet = member.toTypeSetEntity(doc.getDocId());
                    typeSetRepository.save(typeSet);
                }
            }

            List<MethodDto> methodList = docdto.getMethodList();
            Doc finalDoc = doc;
            if (methodList != null) {
                methodList.forEach((methodDto) -> {
                    Method method = methodDto.toEntity(finalDoc.getDocId()); //클래스안의 기본 정보저장
                    method = methodRepository.save(method);
                    List<TypeSetDto> methodDtoList = methodDto.getParamList(); //메서드의 매개변수

                    Method finalMethod = method;
                    methodDtoList.forEach((typeSetDto) -> { //파라미터 정보를 저장
                        Param param = typeSetDto.toParamEntity(finalMethod.getMethodId());//클래스->메서드 -> 파라미터
                        paramRepository.save(param);

                    });

                });
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }


    }
    @Transactional(readOnly = false)
    public List<String> getRepoList(String userId) throws IOException {
        List<String> repoList = new ArrayList<>();
        repoList = docRepository.findByUserId(userId);
//        docList.forEach((doc)->{
//            repoList.add(doc.getRepoName());
//        });

        return repoList;
    }
    @Transactional(readOnly = false)
    public int saveMethodDoc(DocDto docDto){

        Doc doc = docDto.toEntity();
        doc.setDate(LocalDate.now());
        try {
            doc = docRepository.save(doc);
            System.out.println("id : " + doc.getDocId());

            //파라미터 필드
            List<TypeSetDto> paramList = docDto.getMPList();

            if (paramList != null) {
                System.out.println("param size : " + paramList.size());
                for (TypeSetDto param : paramList) {
                    System.out.println("method param : ");
                    System.out.println(param.toString());
                    Param2 param2 = param.toParam2Entity(doc.getDocId());
                    param2Repository.save(param2);
                }
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }
    @Transactional(readOnly = false)
    public String storeToServer(String content){
        String fileAddress = null;

        Gson gson = new Gson();
        String newContent = gson.fromJson(content, String.class);


        //리눅스에 맞게 주소변경
        try{
            String filePath = "D:/E207";
            File folder = new File(filePath + "/CodeDocument");
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("CodeDocument 생성");
                }else{
                    System.out.println("파일생성 안됨");
                }
            }

            fileAddress = filePath+"/CodeDocument/hohoho1.md";
            System.out.println("fileAddress : " + fileAddress);
            File codedoc = new File(fileAddress);

            FileWriter writer = new FileWriter(fileAddress);
            writer.write(newContent);
            writer.close();
            System.out.println("마크다운 문서 작성 완료");


            TmpDocDto tmpDocDto = new TmpDocDto();
            tmpDocDto.setFilePath(fileAddress);
            tmpDocDto.setId(1);
            TmpDoc tmpDoc = tmpDocDto.toEntity();
            tmpDocRepository.save(tmpDoc);
        }catch(IOException ioe){
            System.out.println("마크다운 문서 작성 에러");
            ioe.printStackTrace();
        }



        return tmpDocRepository.findById(1).get().getFilePath();
        //return fileAddress;
    }

    public String getDocAddress(String request){

        //TmpDocDto tmpDocDto = new TmpDocDto();
        //tmpDocDto = tmpDocRepository.getReferenceById(1).TmpDocToDto();
        //파일의 내용을 읽어와서 html로 변환한 후에 넘겨준다.

        //String filePath = tmpDocDto.getFilePath();
        String filePath = "D:/E207/CodeDocument/hohoho.md";
        String content =null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        // Markdown을 HTML로 변환
        String html = renderer.render(parser.parse(content));


        return html;
    }







}
