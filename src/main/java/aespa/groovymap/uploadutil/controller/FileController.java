package aespa.groovymap.uploadutil.controller;

import aespa.groovymap.uploadutil.service.VideoService;
import aespa.groovymap.uploadutil.util.FileUpload;
import java.io.IOException;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileUpload fileUpload;
    private final VideoService videoService;

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource getImage(@PathVariable("filename") String filename) throws MalformedURLException {
        log.info("이미지 요청");
        return new UrlResource("file:" + fileUpload.getFullPath(filename));
    }

    @GetMapping("/videos/{filename}")
    public ResponseEntity<ResourceRegion> getMp4ById(@RequestHeader HttpHeaders headers,
                                                     @PathVariable("filename") String filename)
            throws IOException {
        log.info("영상 요청");
        UrlResource video = new UrlResource(
                "file:" + fileUpload.getFullPath(filename));
        ResourceRegion resourceRegion = videoService.getMp4ResourceRegion(video, headers);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }
}
