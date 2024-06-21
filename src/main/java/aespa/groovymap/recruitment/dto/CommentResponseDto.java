package aespa.groovymap.recruitment.dto;

import aespa.groovymap.domain.Comment;
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

    public CommentResponseDto(Long id, Long postId, String content, String author, ZonedDateTime timestamp) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
    }

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getCommentPost().getId();
        this.author = String.valueOf(comment.getCommentAuthor());
        this.content = comment.getContent();
        this.timestamp = comment.getTimestamp();
    }
}
