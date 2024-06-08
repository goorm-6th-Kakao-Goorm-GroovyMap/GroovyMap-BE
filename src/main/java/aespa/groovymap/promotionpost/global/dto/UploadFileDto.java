package aespa.groovymap.promotionpost.global.dto;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileDto {

    private List<MultipartFile> files;
}
