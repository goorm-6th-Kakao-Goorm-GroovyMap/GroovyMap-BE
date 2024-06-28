package aespa.groovymap.upload.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UploadFileDto {

    private List<MultipartFile> files;
}
