package com.mobilesuit.clientplugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.mobilesuit.clientplugin.gpt.repository.ResponseRepository;
import lombok.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class ResponseTime {
    long reponseTime;
    long requestTime;
    long waitTime;

    long gap; //현재 값
    long readGap; // 읽어온값, 최신값

    public String getWaitTime(){
        long waitMin = TimeUnit.MILLISECONDS.toMinutes(readGap);
        long waitSec = TimeUnit.MILLISECONDS.toSeconds(readGap) -
                TimeUnit.MINUTES.toSeconds(waitMin);

        StringBuilder sb = new StringBuilder();
        sb.append("평균 응답시간 : ").append(waitMin).append("분 ").append(waitSec).append("초");
        return sb.toString();
    }

    public Long updateDate(){
        gap =  reponseTime - requestTime;
        if(readGap == -1){
            readGap = gap;
        }else{
            readGap = (readGap + gap)/2;
        }
        return readGap;
    }
    public long getData() throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        String filePath = currentDirectory + "/waitTime.txt";

        int res =0;

        File waitFile = new File(filePath);
        if(!waitFile.exists()){ // 이미 있는경우 읽어온다.
            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path);

            String stringValue = lines.get(0).trim();
            System.out.println(stringValue);
            readGap = Long.parseLong(stringValue);
        }else{ //없다면 만들기만한다.
            readGap =-1 ;// 무시하도록
            FileWriter writer = new FileWriter(filePath);
            writer.write(-1);
            writer.close();
        }
        return res;
    }

    public long save() throws IOException {
        //파일 생성
        String currentDirectory = System.getProperty("user.dir");
        String filePath = currentDirectory + "/waitTime.txt";

        File waitFile = new File(filePath);
        if(!waitFile.exists()) {
            FileWriter writer = new FileWriter(filePath);
            String stringGap = Long.toString(readGap);
            writer.write(stringGap);
            writer.close();
        }
        return readGap;
    }

}
