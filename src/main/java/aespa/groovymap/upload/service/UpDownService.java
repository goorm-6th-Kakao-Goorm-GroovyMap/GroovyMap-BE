package aespa.groovymap.upload.service;

import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Slf4j
public class UpDownService {

    private String uploadPath = "C:\\upload"; // 업로드 경로

    // 파일 업로드를 처리하는 메서드
    public List<UploadResultDto> uploadFiles(UploadFileDto uploadFileDto) {
        log.info(String.valueOf(uploadFileDto)); // 업로드된 파일 정보 로그 출력

        // 업로드된 파일이 존재하는지 확인
        if (uploadFileDto.getFiles() == null || uploadFileDto.getFiles().isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다."); // 파일이 없을 경우 예외 발생
        }

        final List<UploadResultDto> list = new ArrayList<>(); // 결과를 담을 리스트

        uploadFileDto.getFiles().forEach(multipartFile -> {
            String originalName = multipartFile.getOriginalFilename(); // 원본 파일 이름
            log.info(originalName);

            if (originalName == null || originalName.isEmpty()) {
                log.warn("파일 이름이 유효하지 않습니다.");
                return; // 파일 이름이 유효하지 않으면 처리하지 않음
            }

            String uuid = UUID.randomUUID().toString(); // UUID 생성

            Path savePath = Paths.get(uploadPath, uuid + "_" + originalName); // 저장 경로 설정

            boolean image = false; // 이미지 여부 확인

            try {
                multipartFile.transferTo(savePath); // 파일 저장

                // 이미지 파일인지 확인
                if (Files.probeContentType(savePath).startsWith("image")) {
                    image = true;

                    // 이미지 파일이면 썸네일 생성
                    File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                }
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생: {}", e.getMessage());
                throw new RuntimeException("파일 저장 중 오류가 발생했습니다."); // 예외 발생 시 런타임 예외 던짐
            }

            // 결과 리스트에 추가
            list.add(UploadResultDto.builder()
                    .fileName(uuid + "_" + originalName)
                    .img(image)
                    .filePath(savePath.toString())
                    .fileType(multipartFile.getContentType())
                    .build());
        });
        return list; // 결과 반환
    }

    // 파일 조회를 처리하는 메서드
    public ResponseEntity<Resource> viewFile(String fileNames) {
        Resource resource = new FileSystemResource(uploadPath + "\\" + fileNames); // 파일 리소스 생성
        log.info("Attempting to view file at path: " + fileNames);
        if (!resource.exists()) {
            log.error("파일이 존재하지 않습니다: " + fileNames);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 파일이 없을 시 404 응답
        }
        String resourceName = resource.getFilename(); // 파일 이름 가져오기
        HttpHeaders headers = new HttpHeaders(); // HTTP 헤더 생성

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath())); // 파일의 Content-Type 설정
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build(); // 에러 발생 시 500 응답
        }
        // 파일 조회 성공 시 로그 출력
        log.info("파일 조회 성공: " + resourceName);

        return ResponseEntity.ok().headers(headers).body(resource); // 정상 응답
    }
    // 2번째 버전(스트리밍 방식)
//    public ResponseEntity<StreamingResponseBody> viewFileGET(@PathVariable String fileNames,
//                                                             @RequestHeader(value = "Range", required = false) String rangeHeader) {
//        HttpHeaders headers; // 헤더 설정을 위한 HttpHeaders 객체
//        Resource resource; // 파일 리소스
//        try {
//            // 파일 시스템 리소스를 생성하여 파일 경로를 설정
//            resource = new FileSystemResource(uploadPath + "\\" + fileNames);
//            Path filePath = resource.getFile().toPath();
//
//            // 파일의 크기를 가져옴
//            long fileLength = Files.size(filePath);
//
//            // HttpHeaders 객체 생성
//            headers = new HttpHeaders();
//
//            // 헤더에 파일의 MIME 타입(Content-Type)을 가져와 설정
//            headers.add("Content-Type", Files.probeContentType(filePath));
//
//            // Accept-Ranges 헤더 추가하여 클라이언트가 범위 요청을 할 수 있게 함
//            headers.add("Accept-Ranges", "bytes");
//
//            headers.add("Connection", "keep-alive"); // Connection: keep-alive 헤더 추가
//
//            // Range 요청 헤더가 없는 경우(이미지 파일) 전체 파일을 반환
//            if (rangeHeader == null) {
//                headers.setContentLength(fileLength);
//                return ResponseEntity.ok()
//                        .headers(headers)
//                        .body(outputStream -> {
//                            try (InputStream inputStream = Files.newInputStream(filePath)) {
//                                byte[] buffer = new byte[1024];
//                                int bytesRead;
//                                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                                    outputStream.write(buffer, 0, bytesRead);
//                                }
//                                log.info("파일 조회 성공: {}", fileNames); // 파일 조회 성공 로그 출력
//                            } catch (IOException e) {
//                                log.error("파일 전송 중 오류 발생: {}", e.getMessage());
//                                // 연결이 끊어진 경우 예외 처리
//                                if (e.getMessage().contains("사용자의 호스트 시스템의 소프트웨어의 의해 중단되었습니다")) {
//                                    log.warn("클라이언트가 연결을 중단했습니다: {}", e.getMessage());
//                                } else {
//                                    throw e;
//                                }
//                            }
//                        });
//            }
//
//            // Range 요청 헤더가 있는 경우(동영상 파일) 범위를 파싱하여 시작 및 종료 위치를 결정
//            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
//            long start = Long.parseLong(ranges[0]);
//            long end = (ranges.length > 1) ? Long.parseLong(ranges[1]) : fileLength - 1;
//            long contentLength = end - start + 1;
//
//            // Content-Range 헤더 설정
//            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
//
//            // 부분 콘텐츠(206 상태 코드)를 반환
//            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                    .headers(headers)
//                    .contentLength(contentLength)
//                    .body(outputStream -> {
//                        // 파일에서 지정된 범위만큼 데이터를 읽어 응답으로 전송
//                        try (InputStream inputStream = Files.newInputStream(filePath)) {
//                            inputStream.skip(start);
//                            byte[] buffer = new byte[1024];
//                            long bytesToRead = contentLength;
//                            int bytesRead;
//                            while (bytesToRead > 0 && (bytesRead = inputStream.read(buffer, 0,
//                                    (int) Math.min(buffer.length, bytesToRead))) != -1) {
//                                outputStream.write(buffer, 0, bytesRead);
//                                bytesToRead -= bytesRead;
//                            }
//                            log.info("동영상 파일 조회 성공: {}", fileNames); // 동영상 파일 조회 성공 로그 출력
//                        } catch (IOException e) {
//                            log.error("파일 전송 중 오류 발생: {}", e.getMessage());
//                            // 연결이 끊어진 경우 예외 처리
//                            if (e.getMessage().contains("사용자의 호스트 시스템의 소프트웨어의 의해 중단되었습니다")) {
//                                log.warn("클라이언트가 연결을 중단했습니다: {}", e.getMessage());
//                            } else {
//                                throw e;
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            log.error("파일 조회 중 오류 발생: {}", e.getMessage());
//            return ResponseEntity.internalServerError().build(); // 에러 발생 시 500 응답
//        }
//    }

    // 파일 삭제를 처리하는 메서드
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
