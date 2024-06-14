package aespa.groovymap.recruitment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequestDto {
    private Long postId;
    private Long authorId; // author의 ID로 수정
    private String content;

}
