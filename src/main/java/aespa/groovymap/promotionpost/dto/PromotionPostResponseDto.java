package aespa.groovymap.promotionpost.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PromotionPostResponseDto {
    private String title; // 제목
    private String content; // 내용
    // private Member author; // 작성자
    private Category part; // 유형
    private String region; // 활동지역명
    private String coordinates; // 좌표
    private List<MultipartFile> fileNames; // 첨부파일의 이름들

    public Coordinate getCoordinateObject() {
        // JSON 문자열을 Coordinate 객체로 변환하는 로직
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(coordinates, Coordinate.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse coordinate", e);
        }
    }


}
