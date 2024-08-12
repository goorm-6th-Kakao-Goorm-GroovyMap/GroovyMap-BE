package aespa.groovymap.upload.service;

import aespa.groovymap.upload.dto.SingleFileDto;
import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
//@Profile("aws")
@RequiredArgsConstructor
public class S3UploadService implements UpDownService {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    @Value("${base.url}")
    private String baseUrl;


    // 단일 파일 업로드 메서드
    @Override
    public UploadResultDto uploadSingleFile(SingleFileDto singleFileDto) {
        return uploadFile(singleFileDto.getFile());
    }

    // 다중 파일 업로드 메서드
    @Override
    public List<UploadResultDto> uploadFiles(UploadFileDto uploadFileDto) {
        return uploadFileDto.getFiles().stream().map(this::uploadFile).collect(Collectors.toList());
    }


    // 파일 업로드 메서드
    private UploadResultDto uploadFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return UploadResultDto.builder()
                    .fileName(fileName)
                    .filePath(amazonS3.getUrl(bucketName, fileName).toString())
                    .fileType(multipartFile.getContentType())
                    .img(multipartFile.getContentType().startsWith("image"))
                    .build();
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public ResponseEntity<?> viewFile(String fileNames) {
        String fileName = baseUrl + fileNames;

        return ResponseEntity.ok(fileName);
    }

    @Override
    public Map<String, Boolean> removeFile(String fileNames) {
        Map<String, Boolean> resultMap = new HashMap<>();

        // 여러 파일 이름이 콤마로 구분되어 들어오는 경우를 처리
        String[] fileNameArray = fileNames.split(",");

        for (String fileName : fileNameArray) {
            try {
                // S3 버킷에서 파일 삭제
                amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName.trim()));
                resultMap.put(fileName, true); // 삭제 성공
            } catch (AmazonServiceException e) {
                log.error("Failed to delete file from S3: {}", fileName, e);
                resultMap.put(fileName, false); // 삭제 실패
            }
        }

        return resultMap;
    }
}
