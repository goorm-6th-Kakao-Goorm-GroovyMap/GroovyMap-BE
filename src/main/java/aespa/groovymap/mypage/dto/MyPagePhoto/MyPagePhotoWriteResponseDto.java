package aespa.groovymap.mypage.dto.MyPagePhoto;

import java.util.List;
import lombok.Data;

@Data
public class MyPagePhotoWriteResponseDto {
    private Long id;
    private String text;
    private String image;
    private Long likes;
    private List<ResponseComment> comments;
}
