package aespa.groovymap.mypage.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Type;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MyPageInfoUpdateRequestDto {
    private MultipartFile profileImage;
    private String nickname;
    private String region;
    private Category part;
    private Type type;
    private String introduction;
}
