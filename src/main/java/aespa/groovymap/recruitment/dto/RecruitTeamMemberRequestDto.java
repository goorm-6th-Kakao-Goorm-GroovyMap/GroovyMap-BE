package aespa.groovymap.recruitment.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.Type;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class RecruitTeamMemberRequestDto {
    private Long id; //아이디
    private String title; //제목
    private Member author; //글쓴이
    private String content; //내용
    private String region; //활동지역명
    private Category field; //분야
    private Type part; //유형
//    private Coordinate coordinates; //좌표
    private Integer recruitNum; // 모집 인원
    private ZonedDateTime timeStamp; // 날짜
    private Integer viewCount; // 조회수
}
