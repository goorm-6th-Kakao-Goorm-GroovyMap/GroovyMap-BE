package aespa.groovymap.mypage.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Type;
import lombok.Data;

@Data
public class MyPageInfoUpdateResponseDto {
    private String profileImage;
    private String nickname;
    private String region;
    private Category part;
    private Type type;
    private String introduction;
}
