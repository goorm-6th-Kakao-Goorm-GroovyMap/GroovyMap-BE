package aespa.groovymap.uploadutil.controller;

import java.net.MalformedURLException;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileController {

    @ResponseBody
    @GetMapping("/images/{filename}")
    public UrlResource getImage(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + filename);
    }
}
