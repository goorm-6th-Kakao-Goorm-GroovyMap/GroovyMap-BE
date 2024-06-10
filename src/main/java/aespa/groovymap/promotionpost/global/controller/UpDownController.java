package aespa.groovymap.promotionpost.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UpDownController {

    private String uploadPath = "C:\\upload"; // 업로드 경로

//    @Operation(summary = "파일 업로드", description = "POST 방식으로 파일을 업로드합니다.")
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<List<UploadResultDto>> upload(@ModelAttribute UploadFileDto uploadFileDto) {
//        log.info(String.valueOf(uploadFileDto)); // 업로드된 파일 정보 로그 출력
//
//        // 업로드된 파일이 존재하는지 확인
//        if (uploadFileDto.getFiles() == null || uploadFileDto.getFiles().isEmpty()) {
//            return ResponseEntity.badRequest().body(null); // 파일이 없을 경우 400 응답
//        }
//
//        final List<UploadResultDto> list = new ArrayList<>(); // 결과를 담을 리스트
//
//        uploadFileDto.getFiles().forEach(multipartFile -> {
//            String originalName = multipartFile.getOriginalFilename(); // 원본 파일 이름
//            log.info(originalName);
//
//            if (originalName == null || originalName.isEmpty()) {
//                log.warn("파일 이름이 유효하지 않습니다.");
//                return; // 파일 이름이 유효하지 않으면 처리하지 않음
//            }
//
//            String uuid = UUID.randomUUID().toString(); // UUID 생성
//
//            Path savePath = Paths.get(uploadPath, uuid + "_" + originalName); // 저장 경로 설정
//
//            boolean image = false; // 이미지 여부 확인
//
//            try {
//                multipartFile.transferTo(savePath); // 파일 저장
//
//                // 이미지 파일인지 확인
//                if (Files.probeContentType(savePath).startsWith("image")) {
//                    image = true;
//
//                    // 이미지 파일이면 썸네일 생성
//                    File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
//                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
//                }
//            } catch (IOException e) {
//                log.error("파일 저장 중 오류 발생: {}", e.getMessage());
//                throw new RuntimeException("파일 저장 중 오류가 발생했습니다."); // 예외 발생 시 런타임 예외 던짐
//            }
//
//            // 결과 리스트에 추가
//            list.add(UploadResultDto.builder()
//                    .fileName(uuid + "_" + originalName)
//                    .img(image)
//                    .filePath(savePath.toString())
//                    .fileType(multipartFile.getContentType())
//                    .build());
//        });
//        return ResponseEntity.ok(list); // 결과 반환
//    }

    @Operation(summary = "view 파일", description = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileNames}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileNames) {

        Resource resource = new FileSystemResource(uploadPath + "\\" + fileNames); // 파일 리소스 생성
        String resourceName = resource.getFilename(); // 파일 이름 가져오기
        HttpHeaders headers = new HttpHeaders(); // HTTP 헤더 생성

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath())); // 파일의 Content-Type 설정
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build(); // 에러 발생 시 500 응답
        }
        return ResponseEntity.ok().headers(headers).body(resource); // 정상 응답
    }

    @Operation(summary = "remove 파일", description = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileNames}")
    public Map<String, Boolean> removeFile(@PathVariable String fileNames) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileNames); // 파일 리소스 생성
        String resourceName = resource.getFilename(); // 파일 이름 가져오기

        Map<String, Boolean> resultMap = new HashMap<>(); // 결과 맵 생성
        boolean removed = false; // 삭제 여부

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath()); // 파일의 Content-Type 가져오기
            removed = resource.getFile().delete(); // 파일 삭제

            //섬네일이 존재한다면
            if (contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileNames);
                thumbnailFile.delete(); // 섬네일 파일 삭제
            }

        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
        }

        resultMap.put("result", removed); // 결과 맵에 삭제 여부 추가

        return resultMap; // 결과 반환
    }


}
