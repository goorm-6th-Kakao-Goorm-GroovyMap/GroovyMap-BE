package aespa.groovymap.uploadutil.util;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@PropertySource("classpath:application-file.properties")
public class LocalFileUpload implements FileUpload {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    @Override
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String fullPath = "";
        if (!multipartFile.isEmpty()) {
            fullPath = multipartFile.getOriginalFilename();
            multipartFile.transferTo(new File(getFullPath(fullPath)));
        }

        String fileExtension = fullPath.substring(fullPath.lastIndexOf(".") + 1);
        if (fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif")
                || fileExtension.equals("webp") || fileExtension.equals("svg") || fileExtension.equals("avif")) {
            return "/images/" + fullPath;
        } else if (fileExtension.equals("mp4") || fileExtension.equals("mov")
                || fileExtension.equals("avi") || fileExtension.equals("mkv")) {
            return "/videos/" + fullPath;
        } else {
            return "/images/" + fullPath;
        }
    }

}
