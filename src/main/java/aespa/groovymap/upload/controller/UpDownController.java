package aespa.groovymap.upload.controller;

import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import aespa.groovymap.upload.service.UpDownService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor // 의존성 주입을 위한 롬복 애너테이션
public class UpDownController {

    private final UpDownService uploadService; // 서비스 클래스 의존성 주입

    // 파일 업로드를 처리하는 엔드포인트
    @Operation(summary = "파일 업로드", description = "POST 방식으로 파일을 업로드합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadResultDto>> upload(@ModelAttribute UploadFileDto uploadFileDto) {
        try {
            // 서비스 클래스의 메서드를 호출하여 파일 업로드 처리
            List<UploadResultDto> result = uploadService.uploadFiles(uploadFileDto);
            return ResponseEntity.ok(result); // 업로드된 파일 리스트를 반환
        } catch (IllegalArgumentException e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // 파일이 없을 경우 400 응답
        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(null); // 다른 오류 발생 시 500 응답
        }
    }

    // 파일 조회를 처리하는 엔드포인트
    @Operation(summary = "view 파일", description = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileNames}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileNames) {
        try {
            // 서비스 클래스의 메서드를 호출하여 파일 조회 처리
            return uploadService.viewFile(fileNames);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build(); // 에러 발생 시 500 응답
        }
    }

    // 파일 삭제를 처리하는 엔드포인트
    @Operation(summary = "remove 파일", description = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileNames}")
    public ResponseEntity<Map<String, Boolean>> removeFile(@PathVariable String fileNames) {
        try {
            // 서비스 클래스의 메서드를 호출하여 파일 삭제 처리
            Map<String, Boolean> result = uploadService.removeFile(fileNames);
            return ResponseEntity.ok(result); // 삭제 결과 반환
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(null); // 에러 발생 시 500 응답
        }
    }
}