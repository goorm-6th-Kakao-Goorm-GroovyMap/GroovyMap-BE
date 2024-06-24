package aespa.groovymap.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDto {

    private String fileName;

    private String filePath;

    private String fileType;

    private boolean img;

}
