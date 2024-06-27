package aespa.groovymap.mypage.dto.MyPagePhoto;

import lombok.Data;

@Data
public class MyPagePhotoComment {
    private Long id;
    private String text;
    private String userNickname;
    private String userProfileImage;
}
