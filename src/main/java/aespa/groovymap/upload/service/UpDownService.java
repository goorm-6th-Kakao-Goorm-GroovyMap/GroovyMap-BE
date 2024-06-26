package aespa.groovymap.upload.service;

import aespa.groovymap.upload.dto.SingleFileDto;
import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface UpDownService {

    UploadResultDto uploadSingleFile(SingleFileDto singleFileDto); // 단일 파일 업로드 메서드

    List<UploadResultDto> uploadFiles(UploadFileDto uploadFileDto); // 다중 파일 업로드 메서드

    ResponseEntity<?> viewFile(String fileNames); // 파일 조회 메서드

    Map<String, Boolean> removeFile(String fileNames); // 파일 삭제 메서드


}
