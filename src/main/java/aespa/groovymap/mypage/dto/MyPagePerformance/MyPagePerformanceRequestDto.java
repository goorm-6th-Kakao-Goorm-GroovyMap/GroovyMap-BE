package aespa.groovymap.mypage.dto.MyPagePerformance;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Type;
import lombok.Data;

@Data
public class MyPagePerformanceRequestDto {
    private String description;
    private String address;
    private String date;
    private Category part;
    private Type type;
    private String region;
    private Double latitude;
    private Double longitude;
}
