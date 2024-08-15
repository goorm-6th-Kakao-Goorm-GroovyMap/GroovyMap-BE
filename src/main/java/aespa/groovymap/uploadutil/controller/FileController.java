package aespa.groovymap.uploadutil.controller;

import lombok.extern.slf4j.Slf4j;

//@Controller
@Slf4j
//@RequiredArgsConstructor
public class FileController {

//    private final FileUpload fileUpload;
//    private final VideoService videoService;
//
//    @ResponseBody
//    @GetMapping("/images/{filename}")
//    public Resource getImage(@PathVariable("filename") String filename) throws MalformedURLException {
//        log.info("이미지 요청");
//        return new UrlResource("file:" + fileUpload.getFullPath(filename));
//    }
//
//    @GetMapping("/videos/{filename}")
//    public ResponseEntity<ResourceRegion> getMp4ById(@RequestHeader HttpHeaders headers,
//                                                     @PathVariable("filename") String filename)
//            throws IOException {
//        log.info("영상 요청");
//        UrlResource video = new UrlResource(
//                "file:" + fileUpload.getFullPath(filename));
//        ResourceRegion resourceRegion = videoService.getMp4ResourceRegion(video, headers);
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .body(resourceRegion);
//    }
}
