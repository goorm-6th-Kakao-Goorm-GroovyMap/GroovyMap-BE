package aespa.groovymap.promotionpost.dto;

import aespa.groovymap.domain.Category;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PromotionPostResponseDto {
    private String title; // 제목
    private String content; // 내용
    private Category part; // 유형
    private String region; // 활동지역명
    private String coordinates; // 좌표
    private List<MultipartFile> fileNames; // 첨부파일의 이름들


}
