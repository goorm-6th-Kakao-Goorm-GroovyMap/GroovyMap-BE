package aespa.groovymap.upload.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SingleFileDto {

    MultipartFile file;
}
