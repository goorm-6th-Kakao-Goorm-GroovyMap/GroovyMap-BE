package aespa.groovymap.upload.service;

import aespa.groovymap.upload.dto.UploadFileDto;
import aespa.groovymap.upload.dto.UploadResultDto;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("aws")
public class S3UploadService implements UpDownService {
    @Override
    public List<UploadResultDto> uploadFiles(UploadFileDto uploadFileDto) {
        return null;
    }

    @Override
    public ResponseEntity<Resource> viewFile(String fileNames) {
        return null;
    }

    @Override
    public Map<String, Boolean> removeFile(String fileNames) {
        return null;
    }
}
