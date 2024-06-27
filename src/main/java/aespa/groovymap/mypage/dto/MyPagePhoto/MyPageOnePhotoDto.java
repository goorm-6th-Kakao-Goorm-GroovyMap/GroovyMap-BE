package aespa.groovymap.mypage.dto.MyPagePhoto;

import java.util.List;
import lombok.Data;

@Data
public class MyPageOnePhotoDto {
    private Long id;
    private String text;
    private String image;
    private Integer likes;
    private List<MyPagePhotoComment> comments;
}
