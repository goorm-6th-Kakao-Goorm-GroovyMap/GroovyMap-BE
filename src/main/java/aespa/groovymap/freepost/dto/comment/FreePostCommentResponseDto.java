package aespa.groovymap.freepost.dto.comment;

import aespa.groovymap.domain.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class FreePostCommentResponseDto {
    private Long id;
    private Long postId;
    private String content;
    private String author;
    private ZonedDateTime timestamp;

    public FreePostCommentResponseDto(Long id, Long postId, String content, String author, ZonedDateTime timestamp) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
    }

    public FreePostCommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getCommentPost().getId();
        this.author = String.valueOf(comment.getCommentAuthor());
        this.content = comment.getContent();
        this.timestamp = comment.getTimestamp();
    }
}
