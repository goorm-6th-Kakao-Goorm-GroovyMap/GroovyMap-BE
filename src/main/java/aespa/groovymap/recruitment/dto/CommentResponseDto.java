package aespa.groovymap.recruitment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class CommentResponseDto {
    private Long id;
    private Long postId;
    private String content;
    private String author;
    private ZonedDateTime timestamp;
}
