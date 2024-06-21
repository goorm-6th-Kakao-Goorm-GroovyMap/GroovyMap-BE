package aespa.groovymap.uploadutil.controller;

import java.net.MalformedURLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class FileController {

    @ResponseBody
    @GetMapping("/images/{filename}")
    public UrlResource getImage(@PathVariable("filename") String filename) throws MalformedURLException {
        log.info("이미지 or 영상 요청");
        return new UrlResource("file:" + filename);
    }
}
