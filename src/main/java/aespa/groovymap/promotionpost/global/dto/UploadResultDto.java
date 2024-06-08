package aespa.groovymap.promotionpost.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDto {

    //private Long id;

    //private String uuid;

    private String fileName;

    private String filePath;

    private String fileType;

    private boolean img;

    //    public String getLink() {
//
//        if (img) {
//            return "s_" + uuid + "_" + fileName; //이미지인 경우 섬네일
//        } else {
//            return uuid + "_" + fileName;
//        }
//    }
    public String getLink() {

        if (img) {
            return "s_" + fileName; //이미지인 경우 섬네일
        } else {
            return fileName;
        }
    }
}
