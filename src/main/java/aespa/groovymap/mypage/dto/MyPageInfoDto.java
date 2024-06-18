package aespa.groovymap.mypage.dto;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Type;
import lombok.Data;

@Data
public class MyPageInfoDto {
    private String email;
    private String nickname;
    private String region;
    private Category part;
    private Type type;
    private String profileImage;
    private String introduction;
    private Integer followers;
    private Integer following;
}
