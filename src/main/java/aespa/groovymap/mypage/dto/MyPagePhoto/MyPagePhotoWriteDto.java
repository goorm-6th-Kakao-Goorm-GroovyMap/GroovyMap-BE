package aespa.groovymap.mypage.dto.MyPagePhoto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MyPagePhotoWriteDto {
    private String text;
    private MultipartFile image;
}
