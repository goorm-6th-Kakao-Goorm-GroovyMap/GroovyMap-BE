package aespa.groovymap.mypage.dto.MyPagePhoto;

import java.util.List;
import lombok.Data;

@Data
public class MyPageOnePhotoDto {
    private String text;
    private String image;
    private Integer likes;
    private Boolean isLiked;
    private String createdAt;
    private List<MyPagePhotoComment> comments;
}
