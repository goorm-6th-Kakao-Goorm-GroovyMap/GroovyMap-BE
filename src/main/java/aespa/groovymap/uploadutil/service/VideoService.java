package aespa.groovymap.uploadutil.service;

import java.io.IOException;
import java.util.Optional;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.transaction.annotation.Transactional;

//@Service
@Transactional
public class VideoService {

    public ResourceRegion getMp4ResourceRegion(UrlResource video, HttpHeaders headers) throws IOException {
        final long chunkSize = 1000000L;
        long contentLength = video.contentLength();
        Optional<HttpRange> optional = headers.getRange().stream().findFirst();
        if (optional.isPresent()) {
            HttpRange httpRange = optional.get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            return new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Long.min(chunkSize, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }

}
