package aespa.groovymap.recruitment.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitTeamMemberResponseDto {
    private String title; // 제목
    private Member author; // 글쓴이
    private String content; // 내용
    private String region; // 활동지역명
    private Category field; // 분야
    private Type part; // 유형
//    private String coordinates; // 좌표
    private Integer recruitNum; // 지원 번호

/*
    public Coordinate getCoordinateObject() {
        //JSON 문자열을 Coordinate 객체로 변환하는 로직
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(coordinates, Coordinate.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse coordinate", e);
        }
    }
*/

}
