package aespa.groovymap.mypage.dto.MyPagePhoto;

import lombok.Data;

@Data
public class ResponseComment {
    private Long id;
    private String text;
    private String userNickname;
    private String userProfileImage;
}
