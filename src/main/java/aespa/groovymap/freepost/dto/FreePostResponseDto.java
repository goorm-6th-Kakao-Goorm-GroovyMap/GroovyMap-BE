package aespa.groovymap.freepost.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class FreePostResponseDto {
    private String title; // 제목
    private String content; // 내용
    private String author; // 작성자
    private int savesCount;
    private int likesCount;
    private int viewCount;
    private String timestamp;
//    private Category part; // 유형
//    private String region; // 활동지역명
//    private String coordinates; // 좌표
    private List<MultipartFile> fileNames; // 첨부파일의 이름들

}
