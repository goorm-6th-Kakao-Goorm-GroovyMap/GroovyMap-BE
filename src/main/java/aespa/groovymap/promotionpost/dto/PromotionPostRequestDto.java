package aespa.groovymap.promotionpost.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionPostRequestDto {
    private Long id;
    private String title; // 제목
    private String content; // 내용
    private String author; // 작성자
    private Category part; // 유형
    private String region; // 활동지역명
    private Coordinate coordinates; // 좌표
    private ZonedDateTime timestamp; // 등록시간
    private Integer likesCount; // 좋아요수
    private Integer savesCount; // 저장수
    private Integer viewCount; // 조회수
    private List<String> fileNames; // 첨부파일의 이름들

}
