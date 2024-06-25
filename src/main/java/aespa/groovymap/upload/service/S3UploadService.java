package aespa.groovymap.upload.service;

import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Profile("aws")
@RequiredArgsConstructor
public class S3UploadService implements UpDownService {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    @Value("${base.url}")
    private String baseUrl;

    @Override
    public List<UploadResultDto> uploadFiles(UploadFileDto uploadFileDto) {
        return uploadFileDto.getFiles().stream().map(this::uploadFile).collect(Collectors.toList());
    }

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
        return null;
    }
}
