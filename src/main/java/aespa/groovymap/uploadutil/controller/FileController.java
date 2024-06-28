package aespa.groovymap.uploadutil.controller;

import aespa.groovymap.uploadutil.util.FileUpload;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileUpload fileUpload;

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource getImage(@PathVariable("filename") String filename) throws MalformedURLException {
        log.info("이미지 or 영상 요청");
        return new UrlResource("file:" + fileUpload.getFullPath(filename));
    }
}
